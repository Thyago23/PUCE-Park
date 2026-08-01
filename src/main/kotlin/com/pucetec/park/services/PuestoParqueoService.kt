package com.pucetec.park.services

import com.pucetec.park.dto.*
import com.pucetec.park.entities.EstadoPuesto
import com.pucetec.park.entities.HistorialParqueo
import com.pucetec.park.exceptions.*
import com.pucetec.park.mappers.toEntity
import com.pucetec.park.mappers.toResponse
import com.pucetec.park.repositories.HistorialParqueoRepository
import com.pucetec.park.repositories.PerfilUsuarioRepository
import com.pucetec.park.repositories.PuestoParqueoRepository
import com.pucetec.park.repositories.ZonaParqueoRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class PuestoParqueoService(
    private val puestoParqueoRepository: PuestoParqueoRepository,
    private val zonaParqueoRepository: ZonaParqueoRepository,
    private val historialParqueoRepository: HistorialParqueoRepository,
    private val perfilUsuarioRepository: PerfilUsuarioRepository
) {
    private val logger = LoggerFactory.getLogger(PuestoParqueoService::class.java)

    @Transactional(readOnly = true)
    fun getPuestosByZona(zonaId: Long): List<PuestoParqueoResponse> {
        logger.info("Loading parking spaces for zone id=$zonaId...")
        val puestos = puestoParqueoRepository.findByZonaIdOrderByFilaAscOrdenAsc(zonaId)
        logger.info("Found ${puestos.size} parking space(s) in zone id=$zonaId.")
        return puestos.map { it.toResponse() }
    }

    @Transactional
    fun createPuesto(request: CreatePuestoParqueoRequest): PuestoParqueoResponse {
        logger.info("Creating parking space '${request.numeroPuesto}' in zone id=${request.zonaId}...")
        if (request.numeroPuesto.isBlank()) throw BlankFieldException("numeroPuesto no puede estar vacío")
        logger.info("Loading zone id=${request.zonaId}...")
        val zona = zonaParqueoRepository.findById(request.zonaId).orElseThrow {
            ZonaParqueoNotFoundException("Zona de parqueo ${request.zonaId} no encontrada")
        }
        logger.info("Checking for duplicate space number '${request.numeroPuesto}' in zone '${zona.nombre}'...")
        if (puestoParqueoRepository.existsByNumeroPuestoAndZonaId(request.numeroPuesto, request.zonaId)) {
            throw NumeroPuestoDuplicadoException("Ya existe el puesto '${request.numeroPuesto}' en la zona ${zona.nombre}")
        }
        val count = puestoParqueoRepository.countByZonaId(request.zonaId)
        logger.info("Zone '${zona.nombre}' has $count/${zona.capacidadMaxima} spaces. Checking capacity...")
        if (count >= zona.capacidadMaxima) {
            throw ZonaParqueoLlenaException("La zona ${zona.nombre} ha alcanzado su capacidad máxima de ${zona.capacidadMaxima} puestos")
        }
        logger.info("Saving parking space '${request.numeroPuesto}' to database...")
        val saved = puestoParqueoRepository.save(request.toEntity(zona))
        logger.info("Parking space '${saved.numeroPuesto}' created successfully with id=${saved.id}.")
        return saved.toResponse()
    }

    @Transactional
    fun updatePuesto(id: Long, request: UpdatePuestoParqueoRequest): PuestoParqueoResponse {
        logger.info("Updating parking space id=$id with spaceNumber='${request.numeroPuesto}'...")
        val puesto = puestoParqueoRepository.findById(id).orElseThrow {
            PuestoParqueoNotFoundException("Puesto de parqueo $id no encontrado")
        }
        logger.info("Parking space id=$id found: '${puesto.numeroPuesto}'. Validating new data...")
        if (request.numeroPuesto.isBlank()) throw BlankFieldException("numeroPuesto no puede estar vacío")
        if (puestoParqueoRepository.existsByNumeroPuestoAndZonaIdAndIdNot(request.numeroPuesto, puesto.zona!!.id, id)) {
            throw NumeroPuestoDuplicadoException("Ya existe el puesto '${request.numeroPuesto}' en la zona ${puesto.zona!!.nombre}")
        }
        puesto.numeroPuesto = request.numeroPuesto
        logger.info("Saving updated parking space id=$id...")
        val saved = puestoParqueoRepository.save(puesto)
        logger.info("Parking space id=$id updated successfully.")
        return saved.toResponse()
    }

    @Transactional
    fun getAllPuestos(): List<PuestoParqueoResponse> {
        logger.info("Loading all parking spaces...")
        val puestos = puestoParqueoRepository.findAll()
        logger.info("Found ${puestos.size} total parking space(s).")
        return puestos.map { it.toResponse() }
    }

    @Transactional
    fun ocuparPuesto(id: Long, username: String): PuestoParqueoResponse {
        logger.info("User '$username' is requesting to occupy parking space id=$id...")
        logger.info("Loading user profile for '$username' to verify completeness...")
        val perfil = perfilUsuarioRepository.findByUsername(username).orElse(null)
        if (perfil == null || perfil.placaVehiculo.isBlank() || perfil.nombreCompleto.isBlank() || perfil.numeroPermiso.isNullOrBlank()) {
            logger.warn("User '$username' profile is incomplete. Cannot occupy space id=$id.")
            throw PerfilIncompletoException("Debes completar tu perfil (nombre, placa y número de permiso) antes de ocupar un puesto")
        }
        logger.info("Checking if user '$username' already has an active space...")
        if (historialParqueoRepository.existsByUsernameAndFechaSalidaIsNull(username)) {
            logger.warn("User '$username' already has an active parking space. Cannot occupy another.")
            throw UserAlreadyOccupyingException("Ya tienes un puesto activo. Debes liberarlo antes de ocupar otro.")
        }
        logger.info("Acquiring pessimistic lock on parking space id=$id...")
        val puesto = puestoParqueoRepository.findByIdWithPessimisticLock(id).orElseThrow {
            PuestoParqueoNotFoundException("Puesto de parqueo $id no encontrado")
        }
        if (puesto.estado == EstadoPuesto.OCUPADO) {
            logger.warn("Parking space id=$id is already occupied.")
            throw SlotAlreadyOccupiedException("El puesto $id ya está ocupado")
        }
        puesto.estado = EstadoPuesto.OCUPADO
        puestoParqueoRepository.save(puesto)
        val codigoTicket = "PARK-" + UUID.randomUUID().toString().replace("-", "").take(8).uppercase()
        logger.info("Saving parking history entry for user '$username', space id=$id, ticket='$codigoTicket'...")
        historialParqueoRepository.save(HistorialParqueo(puesto = puesto, username = username, codigoTicket = codigoTicket))
        logger.info("Parking space id=$id successfully occupied by '$username'. Ticket: $codigoTicket.")
        return puesto.toResponse()
    }

    @Transactional
    fun forzarOcupacion(id: Long, placaVehiculo: String, guardUsername: String): PuestoParqueoResponse {
        logger.info("Guard '$guardUsername' is force-occupying parking space id=$id for vehicle '$placaVehiculo'...")
        if (placaVehiculo.isBlank()) throw BlankFieldException("placaVehiculo no puede estar vacío")
        logger.info("Acquiring pessimistic lock on parking space id=$id...")
        val puesto = puestoParqueoRepository.findByIdWithPessimisticLock(id).orElseThrow {
            PuestoParqueoNotFoundException("Puesto de parqueo $id no encontrado")
        }
        if (puesto.estado == EstadoPuesto.OCUPADO) {
            logger.warn("Parking space id=$id is already occupied. Force-occupation by guard '$guardUsername' rejected.")
            throw SlotAlreadyOccupiedException("El puesto $id ya está ocupado")
        }
        puesto.estado = EstadoPuesto.OCUPADO
        puestoParqueoRepository.save(puesto)
        val codigoTicket = "G-${UUID.randomUUID().toString().take(10).uppercase()}"
        logger.info("Saving guard history entry: guard='GUARDIA:$guardUsername', space=$id, plate='$placaVehiculo', ticket='$codigoTicket'...")
        historialParqueoRepository.save(
            HistorialParqueo(puesto = puesto, username = "GUARDIA:$guardUsername", codigoTicket = codigoTicket, placaVehiculo = placaVehiculo)
        )
        logger.info("Parking space id=$id successfully force-occupied by guard '$guardUsername'. Ticket: $codigoTicket.")
        return puesto.toResponse()
    }

    @Transactional
    fun liberarPuesto(id: Long, username: String, isGuard: Boolean): PuestoParqueoResponse {
        logger.info("${if (isGuard) "Guard" else "User"} '$username' is requesting to free parking space id=$id...")
        logger.info("Acquiring pessimistic lock on parking space id=$id...")
        val puesto = puestoParqueoRepository.findByIdWithPessimisticLock(id).orElseThrow {
            PuestoParqueoNotFoundException("Puesto de parqueo $id no encontrado")
        }
        if (puesto.estado == EstadoPuesto.DISPONIBLE) {
            logger.warn("Parking space id=$id is already available. Cannot free it again.")
            throw SlotAlreadyAvailableException("El puesto $id ya está disponible")
        }
        logger.info("Loading active history entry for parking space id=$id...")
        val historial = historialParqueoRepository.findFirstByPuestoIdAndFechaSalidaIsNullOrderByFechaIngresoDesc(id)
            .orElseThrow { HistorialParqueoNotFoundException("Historial activo no encontrado para el puesto $id") }
        if (!isGuard && historial.username != username) {
            logger.warn("Unauthorized attempt: user '$username' tried to free space id=$id owned by '${historial.username}'.")
            throw UnauthorizedAccessException("El usuario $username no está autorizado para liberar este puesto")
        }
        historial.fechaSalida = LocalDateTime.now()
        historialParqueoRepository.save(historial)
        puesto.estado = EstadoPuesto.DISPONIBLE
        logger.info("Parking space id=$id successfully freed by '${if (isGuard) "guard" else "user"} $username'. Exit recorded at ${historial.fechaSalida}.")
        return puestoParqueoRepository.save(puesto).toResponse()
    }
}
