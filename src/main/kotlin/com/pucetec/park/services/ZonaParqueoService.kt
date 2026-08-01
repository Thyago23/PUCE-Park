package com.pucetec.park.services

import com.pucetec.park.dto.*
import com.pucetec.park.entities.EstadoPuesto
import com.pucetec.park.exceptions.*
import com.pucetec.park.mappers.toEntity
import com.pucetec.park.mappers.toResponse
import com.pucetec.park.repositories.PuestoParqueoRepository
import com.pucetec.park.repositories.ZonaParqueoRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ZonaParqueoService(
    private val zonaParqueoRepository: ZonaParqueoRepository,
    private val puestoParqueoRepository: PuestoParqueoRepository
) {
    private val logger = LoggerFactory.getLogger(ZonaParqueoService::class.java)

    @Transactional(readOnly = true)
    fun getAllZonas(): List<ZonaParqueoResponse> {
        logger.info("Loading all parking zones...")
        val zonas = zonaParqueoRepository.findAll()
        logger.info("Found ${zonas.size} parking zone(s). Calculating availability for each zone...")
        val result = zonas.map { zona ->
            val disponibles = puestoParqueoRepository.countByZonaIdAndEstado(zona.id, EstadoPuesto.DISPONIBLE)
            val ocupados = puestoParqueoRepository.countByZonaIdAndEstado(zona.id, EstadoPuesto.OCUPADO)
            logger.info("Zone '${zona.nombre}' → available=$disponibles, occupied=$ocupados")
            zona.toResponse(disponibles, ocupados)
        }
        logger.info("Successfully loaded ${result.size} parking zone(s).")
        return result
    }

    @Transactional
    fun createZona(request: CreateZonaParqueoRequest): ZonaParqueoResponse {
        logger.info("Creating parking zone: name='${request.nombre}', maxCapacity=${request.capacidadMaxima}")
        if (request.nombre.isBlank()) throw BlankFieldException("nombre no puede estar vacío")
        if (request.capacidadMaxima < 1) throw InvalidCapacityException("capacidadMaxima debe ser al menos 1")
        logger.info("Checking for duplicate zone name '${request.nombre}'...")
        if (zonaParqueoRepository.existsByNombre(request.nombre)) {
            throw ZonaParqueoNombreDuplicadoException("Ya existe una zona con el nombre '${request.nombre}'")
        }
        logger.info("Saving new parking zone '${request.nombre}' to database...")
        val saved = zonaParqueoRepository.save(request.toEntity())
        logger.info("Parking zone '${saved.nombre}' created successfully with id=${saved.id}.")
        return saved.toResponse()
    }

    @Transactional
    fun updateZona(id: Long, request: UpdateZonaParqueoRequest): ZonaParqueoResponse {
        logger.info("Updating parking zone id=$id with name='${request.nombre}', maxCapacity=${request.capacidadMaxima}...")
        val zona = zonaParqueoRepository.findById(id).orElseThrow {
            ZonaParqueoNotFoundException("Zona de parqueo $id no encontrada")
        }
        logger.info("Parking zone id=$id found: '${zona.nombre}'. Validating new data...")
        if (request.nombre.isBlank()) throw BlankFieldException("nombre no puede estar vacío")
        if (request.capacidadMaxima < 1) throw InvalidCapacityException("capacidadMaxima debe ser al menos 1")
        if (zonaParqueoRepository.existsByNombreAndIdNot(request.nombre, id)) {
            throw ZonaParqueoNombreDuplicadoException("Ya existe una zona con el nombre '${request.nombre}'")
        }
        zona.nombre = request.nombre
        zona.descripcion = request.descripcion ?: ""
        zona.capacidadMaxima = request.capacidadMaxima
        zona.ubicacion = request.ubicacion ?: zona.ubicacion
        logger.info("Saving updated parking zone id=$id...")
        val saved = zonaParqueoRepository.save(zona)
        logger.info("Parking zone id=$id updated successfully.")
        return saved.toResponse()
    }

    @Transactional
    fun deleteZona(id: Long) {
        logger.info("Deleting parking zone id=$id...")
        val zona = zonaParqueoRepository.findById(id).orElseThrow {
            ZonaParqueoNotFoundException("Zona de parqueo $id no encontrada")
        }
        logger.info("Parking zone id=$id found: '${zona.nombre}'. Checking for assigned spaces...")
        if (puestoParqueoRepository.existsByZonaId(id)) {
            throw ZonaConPuestosException("No se puede eliminar la zona '${zona.nombre}' porque tiene puestos asignados")
        }
        zonaParqueoRepository.delete(zona)
        logger.info("Parking zone '${zona.nombre}' (id=$id) deleted successfully.")
    }

    @Transactional(readOnly = true)
    fun getEstadisticas(id: Long): EstadisticasZonaResponse {
        logger.info("Loading statistics for parking zone id=$id...")
        val zona = zonaParqueoRepository.findById(id).orElseThrow {
            ZonaParqueoNotFoundException("Zona de parqueo $id no encontrada")
        }
        val disponibles = puestoParqueoRepository.countByZonaIdAndEstado(id, EstadoPuesto.DISPONIBLE)
        val ocupados = puestoParqueoRepository.countByZonaIdAndEstado(id, EstadoPuesto.OCUPADO)
        logger.info("Statistics for zone '${zona.nombre}': available=$disponibles, occupied=$ocupados, total=${disponibles + ocupados}.")
        return EstadisticasZonaResponse(
            zonaId = zona.id,
            zonaNombre = zona.nombre,
            capacidadMaxima = zona.capacidadMaxima,
            disponibles = disponibles,
            ocupados = ocupados,
            total = disponibles + ocupados
        )
    }
}
