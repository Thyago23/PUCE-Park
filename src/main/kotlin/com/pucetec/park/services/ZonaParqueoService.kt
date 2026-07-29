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
        logger.info("Getting all parking zones")
        return zonaParqueoRepository.findAll().map { it.toResponse() }
    }

    @Transactional
    fun createZona(request: CreateZonaParqueoRequest): ZonaParqueoResponse {
        logger.info("Creating parking zone: ${request.nombre}")
        if (request.nombre.isBlank()) throw BlankFieldException("nombre no puede estar vacío")
        if (request.capacidadMaxima < 1) throw InvalidCapacityException("capacidadMaxima debe ser al menos 1")
        if (zonaParqueoRepository.existsByNombre(request.nombre)) {
            throw ZonaParqueoNombreDuplicadoException("Ya existe una zona con el nombre '${request.nombre}'")
        }
        return zonaParqueoRepository.save(request.toEntity()).toResponse()
    }

    @Transactional
    fun updateZona(id: Long, request: UpdateZonaParqueoRequest): ZonaParqueoResponse {
        logger.info("Updating parking zone $id")
        val zona = zonaParqueoRepository.findById(id).orElseThrow {
            ZonaParqueoNotFoundException("Zona de parqueo $id no encontrada")
        }
        if (request.nombre.isBlank()) throw BlankFieldException("nombre no puede estar vacío")
        if (request.capacidadMaxima < 1) throw InvalidCapacityException("capacidadMaxima debe ser al menos 1")
        if (zonaParqueoRepository.existsByNombreAndIdNot(request.nombre, id)) {
            throw ZonaParqueoNombreDuplicadoException("Ya existe una zona con el nombre '${request.nombre}'")
        }
        zona.nombre = request.nombre
        zona.descripcion = request.descripcion ?: ""
        zona.capacidadMaxima = request.capacidadMaxima
        return zonaParqueoRepository.save(zona).toResponse()
    }

    @Transactional
    fun deleteZona(id: Long) {
        logger.info("Deleting parking zone $id")
        val zona = zonaParqueoRepository.findById(id).orElseThrow {
            ZonaParqueoNotFoundException("Zona de parqueo $id no encontrada")
        }
        if (puestoParqueoRepository.existsByZonaId(id)) {
            throw ZonaConPuestosException("No se puede eliminar la zona '${zona.nombre}' porque tiene puestos asignados")
        }
        zonaParqueoRepository.delete(zona)
    }

    @Transactional(readOnly = true)
    fun getEstadisticas(id: Long): EstadisticasZonaResponse {
        logger.info("Getting estadisticas for zone $id")
        val zona = zonaParqueoRepository.findById(id).orElseThrow {
            ZonaParqueoNotFoundException("Zona de parqueo $id no encontrada")
        }
        val disponibles = puestoParqueoRepository.countByZonaIdAndEstado(id, EstadoPuesto.DISPONIBLE)
        val ocupados = puestoParqueoRepository.countByZonaIdAndEstado(id, EstadoPuesto.OCUPADO)
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
