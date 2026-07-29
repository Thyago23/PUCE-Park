package com.pucetec.park.controllers

import com.pucetec.park.dto.UpdatePerfilUsuarioRequest
import com.pucetec.park.dto.PerfilUsuarioResponse
import com.pucetec.park.services.PerfilUsuarioService
import org.slf4j.LoggerFactory
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/perfil/me")
class PerfilUsuarioController(
    private val perfilUsuarioService: PerfilUsuarioService
) {
    private val logger = LoggerFactory.getLogger(PerfilUsuarioController::class.java)

    @GetMapping
    fun getPerfil(@AuthenticationPrincipal jwt: Jwt): PerfilUsuarioResponse {
        val username = jwt.getClaimAsString("username") ?: jwt.subject
        logger.info("GET /api/v1/perfil/me - username=$username")
        return perfilUsuarioService.getOrCreatePerfil(username)
    }

    @PutMapping
    fun updatePerfil(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody request: UpdatePerfilUsuarioRequest
    ): PerfilUsuarioResponse {
        val username = jwt.getClaimAsString("username") ?: jwt.subject
        logger.info("PUT /api/v1/perfil/me - username=$username")
        return perfilUsuarioService.updatePerfil(username, request)
    }
}
