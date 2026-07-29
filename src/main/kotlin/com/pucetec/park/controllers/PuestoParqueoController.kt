package com.pucetec.park.controllers

import com.pucetec.park.dto.CreatePuestoParqueoRequest
import com.pucetec.park.dto.PuestoParqueoResponse
import com.pucetec.park.dto.UpdatePuestoParqueoRequest
import com.pucetec.park.services.PuestoParqueoService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/puestos")
class PuestoParqueoController(
    private val puestoParqueoService: PuestoParqueoService
) {
    private val logger = LoggerFactory.getLogger(PuestoParqueoController::class.java)

    @GetMapping("/zona/{zonaId}")
    fun getPuestosByZona(@PathVariable zonaId: Long): List<PuestoParqueoResponse> {
        logger.info("GET /api/v1/puestos/zona/$zonaId")
        return puestoParqueoService.getPuestosByZona(zonaId)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createPuesto(@RequestBody request: CreatePuestoParqueoRequest): PuestoParqueoResponse {
        logger.info("POST /api/v1/puestos - numeroPuesto=${request.numeroPuesto}")
        return puestoParqueoService.createPuesto(request)
    }

    @PutMapping("/{id}")
    fun updatePuesto(@PathVariable id: Long, @RequestBody request: UpdatePuestoParqueoRequest): PuestoParqueoResponse {
        logger.info("PUT /api/v1/puestos/$id - numeroPuesto=${request.numeroPuesto}")
        return puestoParqueoService.updatePuesto(id, request)
    }

    @PutMapping("/{id}/ocupar")
    fun ocuparPuesto(@PathVariable id: Long, @AuthenticationPrincipal jwt: Jwt): PuestoParqueoResponse {
        val username = jwt.getClaimAsString("username") ?: jwt.subject
        logger.info("PUT /api/v1/puestos/$id/ocupar - username=$username")
        return puestoParqueoService.ocuparPuesto(id, username)
    }

    @PutMapping("/{id}/liberar")
    fun liberarPuesto(@PathVariable id: Long, @AuthenticationPrincipal jwt: Jwt): PuestoParqueoResponse {
        val username = jwt.getClaimAsString("username") ?: jwt.subject
        logger.info("PUT /api/v1/puestos/$id/liberar - username=$username")
        return puestoParqueoService.liberarPuesto(id, username, isGuard = false)
    }

    @PutMapping("/{id}/forzar-liberacion")
    fun forzarLiberacion(@PathVariable id: Long, @AuthenticationPrincipal jwt: Jwt): PuestoParqueoResponse {
        val username = jwt.getClaimAsString("username") ?: jwt.subject
        logger.info("PUT /api/v1/puestos/$id/forzar-liberacion - username=$username")
        return puestoParqueoService.liberarPuesto(id, username, isGuard = true)
    }
}
