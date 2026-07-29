package com.pucetec.park.services

import com.pucetec.park.dto.*
import com.pucetec.park.entities.ZonaParqueo
import com.pucetec.park.exceptions.ZonaParqueoNotFoundException
import com.pucetec.park.mappers.toEntity
import com.pucetec.park.mappers.toResponse
import com.pucetec.park.repositories.ZonaParqueoRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ZonaParqueoService(
    private val zonaParqueoRepository: ZonaParqueoRepository
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
        return zonaParqueoRepository.save(request.toEntity()).toResponse()
    }

    @Transactional
    fun updateZona(id: Long, request: UpdateZonaParqueoRequest): ZonaParqueoResponse {
        logger.info("Updating parking zone $id")
        val zona = zonaParqueoRepository.findById(id).orElseThrow {
            ZonaParqueoNotFoundException("Zona de parqueo $id no encontrada")
        }
        zona.nombre = request.nombre
        zona.descripcion = request.descripcion
        return zonaParqueoRepository.save(zona).toResponse()
    }

    @Transactional
    fun deleteZona(id: Long) {
        logger.info("Deleting parking zone $id")
        val zona = zonaParqueoRepository.findById(id).orElseThrow {
            ZonaParqueoNotFoundException("Zona de parqueo $id no encontrada")
        }
        zonaParqueoRepository.delete(zona)
    }
}
