package com.pucetec.park.services

import com.pucetec.park.dto.HistorialParqueoResponse
import com.pucetec.park.exceptions.PuestoParqueoNotFoundException
import com.pucetec.park.mappers.toResponse
import com.pucetec.park.repositories.HistorialParqueoRepository
import com.pucetec.park.repositories.PuestoParqueoRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class HistorialParqueoService(
    private val historialParqueoRepository: HistorialParqueoRepository,
    private val puestoParqueoRepository: PuestoParqueoRepository
) {
    private val logger = LoggerFactory.getLogger(HistorialParqueoService::class.java)

    @Transactional(readOnly = true)
    fun getHistorialByUsername(username: String): List<HistorialParqueoResponse> {
        logger.info("Getting historial for user $username")
        return historialParqueoRepository.findByUsernameOrderByFechaIngresoDesc(username).map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    fun getHistorialByPuesto(puestoId: Long): List<HistorialParqueoResponse> {
        logger.info("Getting historial for parking space $puestoId")
        if (!puestoParqueoRepository.existsById(puestoId)) {
            throw PuestoParqueoNotFoundException("Puesto de parqueo $puestoId no encontrado")
        }
        return historialParqueoRepository.findByPuestoIdOrderByFechaIngresoDesc(puestoId).map { it.toResponse() }
    }
}
