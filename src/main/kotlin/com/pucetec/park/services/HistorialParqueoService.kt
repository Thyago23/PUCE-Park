package com.pucetec.park.services

import com.pucetec.park.dto.EstadisticasPersonalesResponse
import com.pucetec.park.dto.HistorialParqueoResponse
import com.pucetec.park.dto.RankingEntradaResponse
import com.pucetec.park.exceptions.PuestoParqueoNotFoundException
import com.pucetec.park.mappers.toResponse
import com.pucetec.park.repositories.HistorialParqueoRepository
import com.pucetec.park.repositories.PuestoParqueoRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.YearMonth

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
    fun getHistorialByGuardia(guardUsername: String): List<HistorialParqueoResponse> {
        logger.info("Getting guard historial for GUARDIA:$guardUsername")
        return historialParqueoRepository.findByUsernameOrderByFechaIngresoDesc("GUARDIA:$guardUsername").map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    fun getHistorialByPuesto(puestoId: Long): List<HistorialParqueoResponse> {
        logger.info("Getting historial for parking space $puestoId")
        if (!puestoParqueoRepository.existsById(puestoId)) {
            throw PuestoParqueoNotFoundException("Puesto de parqueo $puestoId no encontrado")
        }
        return historialParqueoRepository.findByPuestoIdOrderByFechaIngresoDesc(puestoId).map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    fun getEstadisticasPersonales(username: String, mes: String): EstadisticasPersonalesResponse {
        logger.info("Getting personal stats for $username, mes=$mes")
        val yearMonth = YearMonth.parse(mes)
        val stats = historialParqueoRepository.getEstadisticasPersonales(username, yearMonth.year, yearMonth.monthValue)
        val totalHoras = stats.getTotalHoras() ?: 0.0
        val totalSesiones = stats.getTotalSesiones()
        val promedio = if (totalSesiones > 0) totalHoras / totalSesiones else 0.0
        return EstadisticasPersonalesResponse(
            mes = mes,
            totalSesiones = totalSesiones,
            totalHoras = Math.round(totalHoras * 100.0) / 100.0,
            promedioHorasPorSesion = Math.round(promedio * 100.0) / 100.0
        )
    }

    @Transactional(readOnly = true)
    fun getRankingMensual(mes: String): List<RankingEntradaResponse> {
        logger.info("Getting monthly ranking for mes=$mes")
        val yearMonth = YearMonth.parse(mes)
        return historialParqueoRepository.getRankingMensual(yearMonth.year, yearMonth.monthValue)
            .mapIndexed { index, entry ->
                RankingEntradaResponse(
                    posicion = index + 1,
                    username = entry.getUsername(),
                    nombreCompleto = entry.getNombreCompleto() ?: entry.getUsername(),
                    totalHoras = Math.round(entry.getTotalHoras() * 100.0) / 100.0,
                    totalSesiones = entry.getTotalSesiones()
                )
            }
    }
}
