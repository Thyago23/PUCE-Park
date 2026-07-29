package com.pucetec.park.services

import com.pucetec.park.dto.*
import com.pucetec.park.entities.HistorialParqueo
import com.pucetec.park.entities.PuestoParqueo
import com.pucetec.park.exceptions.*
import com.pucetec.park.mappers.toEntity
import com.pucetec.park.mappers.toResponse
import com.pucetec.park.repositories.HistorialParqueoRepository
import com.pucetec.park.repositories.PuestoParqueoRepository
import com.pucetec.park.repositories.ZonaParqueoRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class PuestoParqueoService(
    private val puestoParqueoRepository: PuestoParqueoRepository,
    private val zonaParqueoRepository: ZonaParqueoRepository,
    private val historialParqueoRepository: HistorialParqueoRepository
) {
    private val logger = LoggerFactory.getLogger(PuestoParqueoService::class.java)

    @Transactional(readOnly = true)
    fun getPuestosByZona(zonaId: Long): List<PuestoParqueoResponse> {
        logger.info("Getting parking spaces for zone $zonaId")
        return puestoParqueoRepository.findByZonaId(zonaId).map { it.toResponse() }
    }

    @Transactional
    fun createPuesto(request: CreatePuestoParqueoRequest): PuestoParqueoResponse {
        logger.info("Creating parking space: ${request.numeroPuesto}")
        val zona = zonaParqueoRepository.findById(request.zonaId).orElseThrow {
            ZonaParqueoNotFoundException("Zona de parqueo ${request.zonaId} no encontrada")
        }
        return puestoParqueoRepository.save(request.toEntity(zona)).toResponse()
    }

    @Transactional
    fun ocuparPuesto(id: Long, username: String): PuestoParqueoResponse {
        logger.info("Occupying parking space $id by user $username")
        val puesto = puestoParqueoRepository.findByIdWithPessimisticLock(id).orElseThrow {
            PuestoParqueoNotFoundException("Puesto de parqueo $id no encontrado")
        }
        if (puesto.estado == "OCUPADO") {
            throw SlotAlreadyOccupiedException("El puesto $id ya está ocupado")
        }
        puesto.estado = "OCUPADO"
        puestoParqueoRepository.save(puesto)

        val historial = HistorialParqueo(puesto = puesto, username = username)
        historialParqueoRepository.save(historial)

        return puesto.toResponse()
    }

    @Transactional
    fun liberarPuesto(id: Long, username: String, isGuard: Boolean): PuestoParqueoResponse {
        logger.info("Freeing parking space $id (isGuard=$isGuard)")
        val puesto = puestoParqueoRepository.findByIdWithPessimisticLock(id).orElseThrow {
            PuestoParqueoNotFoundException("Puesto de parqueo $id no encontrado")
        }
        if (puesto.estado == "DISPONIBLE") {
            throw SlotAlreadyOccupiedException("El puesto $id ya está disponible")
        }

        val historial = historialParqueoRepository.findFirstByPuestoIdAndFechaSalidaIsNullOrderByFechaIngresoDesc(id)
            .orElseThrow { HistorialParqueoNotFoundException("Historial activo no encontrado para el puesto $id") }

        if (!isGuard && historial.username != username) {
            throw UnauthorizedAccessException("El usuario $username no está autorizado para liberar este puesto")
        }

        historial.fechaSalida = LocalDateTime.now()
        historialParqueoRepository.save(historial)

        puesto.estado = "DISPONIBLE"
        return puestoParqueoRepository.save(puesto).toResponse()
    }
}
