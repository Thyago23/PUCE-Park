package com.pucetec.park.controllers

import com.pucetec.park.config.SecurityConfig
import com.pucetec.park.dto.PerfilUsuarioResponse
import com.pucetec.park.exceptions.GlobalExceptionHandler
import com.pucetec.park.services.PerfilUsuarioService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(PerfilUsuarioController::class)
@Import(SecurityConfig::class, GlobalExceptionHandler::class)
class PerfilUsuarioControllerTest {

    @Autowired private lateinit var mockMvc: MockMvc
    @MockitoBean private lateinit var perfilUsuarioService: PerfilUsuarioService
    @MockitoBean private lateinit var jwtDecoder: JwtDecoder

    private val perfilResponse = PerfilUsuarioResponse(
        id = 1L, username = "jdoe",
        nombreCompleto = "John Doe", placaVehiculo = "ABC-123", modoOscuro = false
    )

    @Test
    fun `GET perfil me sin token responde 401`() {
        mockMvc.perform(get("/api/v1/perfil/me"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `GET perfil me con rol ADMIN responde 403`() {
        mockMvc.perform(
            get("/api/v1/perfil/me")
                .with(jwt().authorities(SimpleGrantedAuthority("ROLE_ADMIN")))
        ).andExpect(status().isForbidden)
    }

    @Test
    fun `GET perfil me con rol DRIVER responde 200`() {
        whenever(perfilUsuarioService.getOrCreatePerfil(any())).thenReturn(perfilResponse)

        mockMvc.perform(
            get("/api/v1/perfil/me")
                .with(jwt().authorities(SimpleGrantedAuthority("ROLE_DRIVER")))
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.username").value("jdoe"))
            .andExpect(jsonPath("$.nombreCompleto").value("John Doe"))
    }

    @Test
    fun `PUT perfil me sin token responde 401`() {
        mockMvc.perform(
            put("/api/v1/perfil/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"nombreCompleto":"John","placaVehiculo":"ABC-123","modoOscuro":false}""")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `PUT perfil me con rol GUARD responde 403`() {
        mockMvc.perform(
            put("/api/v1/perfil/me")
                .with(jwt().authorities(SimpleGrantedAuthority("ROLE_GUARD")))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"nombreCompleto":"John","placaVehiculo":"ABC-123","modoOscuro":false}""")
        ).andExpect(status().isForbidden)
    }

    @Test
    fun `PUT perfil me con rol DRIVER responde 200`() {
        whenever(perfilUsuarioService.updatePerfil(any(), any())).thenReturn(perfilResponse)

        mockMvc.perform(
            put("/api/v1/perfil/me")
                .with(jwt().authorities(SimpleGrantedAuthority("ROLE_DRIVER")))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"nombreCompleto":"John Doe","placaVehiculo":"ABC-123","modoOscuro":false}""")
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.nombreCompleto").value("John Doe"))
    }
}
