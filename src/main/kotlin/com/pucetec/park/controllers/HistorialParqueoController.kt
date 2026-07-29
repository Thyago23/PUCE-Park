package com.pucetec.park.controllers

import com.pucetec.park.dto.HistorialParqueoResponse
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
        val username = jwt.getClaimAsString("username") ?: jwt.subject
        logger.info("GET /api/v1/historial/me - username=$username")
        return historialParqueoService.getHistorialByUsername(username)
    }

    @GetMapping("/puesto/{puestoId}")
    fun getHistorialByPuesto(@PathVariable puestoId: Long): List<HistorialParqueoResponse> {
        logger.info("GET /api/v1/historial/puesto/$puestoId")
        return historialParqueoService.getHistorialByPuesto(puestoId)
    }
}
