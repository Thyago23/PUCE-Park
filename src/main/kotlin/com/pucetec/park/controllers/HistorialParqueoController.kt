package com.pucetec.park.controllers

import com.pucetec.park.dto.EstadisticasPersonalesResponse
import com.pucetec.park.dto.HistorialParqueoResponse
import com.pucetec.park.dto.RankingEntradaResponse
import com.pucetec.park.services.HistorialParqueoService
import org.slf4j.LoggerFactory
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/historial")
class HistorialParqueoController(
    private val historialParqueoService: HistorialParqueoService
) {
    private val logger = LoggerFactory.getLogger(HistorialParqueoController::class.java)

    @GetMapping("/me")
    fun getMiHistorial(@AuthenticationPrincipal jwt: Jwt): List<HistorialParqueoResponse> {
        val username = jwt.getClaimAsString("username") ?: jwt.getClaimAsString("cognito:username") ?: jwt.subject
        logger.info("GET /api/v1/historial/me - username=$username")
        return historialParqueoService.getHistorialByUsername(username)
    }

    @GetMapping("/me/estadisticas")
    fun getMisEstadisticas(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestParam mes: String
    ): EstadisticasPersonalesResponse {
        val username = jwt.getClaimAsString("username") ?: jwt.getClaimAsString("cognito:username") ?: jwt.subject
        logger.info("GET /api/v1/historial/me/estadisticas - username=$username, mes=$mes")
        return historialParqueoService.getEstadisticasPersonales(username, mes)
    }

    @GetMapping("/puesto/{puestoId}")
    fun getHistorialByPuesto(@PathVariable puestoId: Long): List<HistorialParqueoResponse> {
        logger.info("GET /api/v1/historial/puesto/$puestoId")
        return historialParqueoService.getHistorialByPuesto(puestoId)
    }

    @GetMapping("/ranking/mensual")
    fun getRankingMensual(@RequestParam mes: String): List<RankingEntradaResponse> {
        logger.info("GET /api/v1/historial/ranking/mensual - mes=$mes")
        return historialParqueoService.getRankingMensual(mes)
    }
}
