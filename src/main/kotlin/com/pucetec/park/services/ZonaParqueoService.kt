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
        logger.info("Creating parking zone: name='${request.name}', maxCapacity=${request.maxCapacity}")
        if (request.name.isBlank()) throw BlankFieldException("name must not be blank")
        if (request.maxCapacity < 1) throw InvalidCapacityException("maxCapacity must be at least 1")
        logger.info("Checking for duplicate zone name '${request.name}'...")
        if (zonaParqueoRepository.existsByNombre(request.name)) {
            throw ZonaParqueoNombreDuplicadoException("A zone named '${request.name}' already exists")
        }
        logger.info("Saving new parking zone '${request.name}' to database...")
        val saved = zonaParqueoRepository.save(request.toEntity())
        logger.info("Parking zone '${saved.nombre}' created successfully with id=${saved.id}.")
        return saved.toResponse()
    }

    @Transactional
    fun updateZona(id: Long, request: UpdateZonaParqueoRequest): ZonaParqueoResponse {
        logger.info("Updating parking zone id=$id with name='${request.name}', maxCapacity=${request.maxCapacity}...")
        val zona = zonaParqueoRepository.findById(id).orElseThrow {
            ZonaParqueoNotFoundException("Parking zone $id not found")
        }
        logger.info("Parking zone id=$id found: '${zona.nombre}'. Validating new data...")
        if (request.name.isBlank()) throw BlankFieldException("name must not be blank")
        if (request.maxCapacity < 1) throw InvalidCapacityException("maxCapacity must be at least 1")
        if (zonaParqueoRepository.existsByNombreAndIdNot(request.name, id)) {
            throw ZonaParqueoNombreDuplicadoException("A zone named '${request.name}' already exists")
        }
        zona.nombre = request.name
        zona.descripcion = request.description ?: ""
        zona.capacidadMaxima = request.maxCapacity
        zona.ubicacion = request.location ?: zona.ubicacion
        logger.info("Saving updated parking zone id=$id...")
        val saved = zonaParqueoRepository.save(zona)
        logger.info("Parking zone id=$id updated successfully.")
        return saved.toResponse()
    }

    @Transactional
    fun deleteZona(id: Long) {
        logger.info("Deleting parking zone id=$id...")
        val zona = zonaParqueoRepository.findById(id).orElseThrow {
            ZonaParqueoNotFoundException("Parking zone $id not found")
        }
        logger.info("Parking zone id=$id found: '${zona.nombre}'. Checking for assigned spaces...")
        if (puestoParqueoRepository.existsByZonaId(id)) {
            throw ZonaConPuestosException("Cannot delete zone '${zona.nombre}' because it has assigned spaces")
        }
        zonaParqueoRepository.delete(zona)
        logger.info("Parking zone '${zona.nombre}' (id=$id) deleted successfully.")
    }

    @Transactional(readOnly = true)
    fun getEstadisticas(id: Long): EstadisticasZonaResponse {
        logger.info("Loading statistics for parking zone id=$id...")
        val zona = zonaParqueoRepository.findById(id).orElseThrow {
            ZonaParqueoNotFoundException("Parking zone $id not found")
        }
        val disponibles = puestoParqueoRepository.countByZonaIdAndEstado(id, EstadoPuesto.DISPONIBLE)
        val ocupados = puestoParqueoRepository.countByZonaIdAndEstado(id, EstadoPuesto.OCUPADO)
        logger.info("Statistics for zone '${zona.nombre}': available=$disponibles, occupied=$ocupados, total=${disponibles + ocupados}.")
        return EstadisticasZonaResponse(
            zoneId = zona.id,
            zoneName = zona.nombre,
            maxCapacity = zona.capacidadMaxima,
            available = disponibles,
            occupied = ocupados,
            total = disponibles + ocupados
        )
    }
}
