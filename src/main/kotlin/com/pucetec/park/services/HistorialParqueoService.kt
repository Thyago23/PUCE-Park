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
        logger.info("Loading parking history for user '$username'...")
        val historial = historialParqueoRepository.findByUsernameOrderByFechaIngresoDesc(username)
        logger.info("Found ${historial.size} history record(s) for user '$username'.")
        return historial.map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    fun getHistorialByGuardia(guardUsername: String): List<HistorialParqueoResponse> {
        logger.info("Loading parking activity for guard '$guardUsername' (stored as 'GUARDIA:$guardUsername')...")
        val historial = historialParqueoRepository.findByUsernameOrderByFechaIngresoDesc("GUARDIA:$guardUsername")
        logger.info("Found ${historial.size} activity record(s) for guard '$guardUsername'.")
        return historial.map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    fun getHistorialByPuesto(puestoId: Long): List<HistorialParqueoResponse> {
        logger.info("Loading parking history for space id=$puestoId...")
        if (!puestoParqueoRepository.existsById(puestoId)) {
            logger.warn("Parking space id=$puestoId not found.")
            throw PuestoParqueoNotFoundException("Parking space $puestoId not found")
        }
        val historial = historialParqueoRepository.findByPuestoIdOrderByFechaIngresoDesc(puestoId)
        logger.info("Found ${historial.size} history record(s) for parking space id=$puestoId.")
        return historial.map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    fun getEstadisticasPersonales(username: String, mes: String): EstadisticasPersonalesResponse {
        logger.info("Loading personal statistics for user '$username', period='$mes'...")
        val yearMonth = YearMonth.parse(mes)
        logger.info("Querying database for stats: year=${yearMonth.year}, month=${yearMonth.monthValue}...")
        val stats = historialParqueoRepository.getEstadisticasPersonales(username, yearMonth.year, yearMonth.monthValue)
        val totalHours = stats.getTotalHoras() ?: 0.0
        val totalSessions = stats.getTotalSesiones()
        val avg = if (totalSessions > 0) totalHours / totalSessions else 0.0
        logger.info("Stats for '$username' in $mes: sessions=$totalSessions, totalHours=$totalHours, avgHoursPerSession=$avg.")
        return EstadisticasPersonalesResponse(
            month = mes,
            totalSessions = totalSessions,
            totalHours = Math.round(totalHours * 100.0) / 100.0,
            avgHoursPerSession = Math.round(avg * 100.0) / 100.0
        )
    }

    @Transactional(readOnly = true)
    fun getRankingMensual(mes: String): List<RankingEntradaResponse> {
        logger.info("Loading monthly ranking for period='$mes'...")
        val yearMonth = YearMonth.parse(mes)
        logger.info("Querying ranking for year=${yearMonth.year}, month=${yearMonth.monthValue} (excluding GUARDIA entries)...")
        val entries = historialParqueoRepository.getRankingMensual(yearMonth.year, yearMonth.monthValue)
        logger.info("Ranking loaded: ${entries.size} user(s) found for $mes.")
        return entries.mapIndexed { index, entry ->
            RankingEntradaResponse(
                position = index + 1,
                username = entry.getUsername(),
                fullName = entry.getNombreCompleto() ?: entry.getUsername(),
                totalHours = Math.round(entry.getTotalHoras() * 100.0) / 100.0,
                totalSessions = entry.getTotalSesiones()
            )
        }
    }
}
