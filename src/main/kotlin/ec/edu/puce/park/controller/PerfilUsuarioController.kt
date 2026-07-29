package ec.edu.puce.park.controller

import ec.edu.puce.park.dto.PerfilUsuarioRequestDTO
import ec.edu.puce.park.dto.PerfilUsuarioResponseDTO
import ec.edu.puce.park.service.PerfilUsuarioService
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/perfil/me")
class PerfilUsuarioController(
    private val perfilUsuarioService: PerfilUsuarioService
) {

    @GetMapping
    fun getPerfil(@AuthenticationPrincipal jwt: Jwt): PerfilUsuarioResponseDTO {
        val username = jwt.getClaimAsString("username") ?: jwt.subject
        return perfilUsuarioService.getOrCreatePerfil(username)
    }

    @PutMapping
    fun updatePerfil(
        @AuthenticationPrincipal jwt: Jwt,
        @Valid @RequestBody request: PerfilUsuarioRequestDTO
    ): PerfilUsuarioResponseDTO {
        val username = jwt.getClaimAsString("username") ?: jwt.subject
        return perfilUsuarioService.updatePerfil(username, request)
    }
}
