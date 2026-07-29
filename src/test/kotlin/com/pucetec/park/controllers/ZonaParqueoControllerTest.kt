package com.pucetec.park.controllers

import com.pucetec.park.config.SecurityConfig
import com.pucetec.park.dto.CreateZonaParqueoRequest
import com.pucetec.park.dto.EstadisticasZonaResponse
import com.pucetec.park.dto.UpdateZonaParqueoRequest
import com.pucetec.park.dto.ZonaParqueoResponse
import com.pucetec.park.exceptions.*
import com.pucetec.park.services.ZonaParqueoService
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

@WebMvcTest(ZonaParqueoController::class)
@Import(SecurityConfig::class, GlobalExceptionHandler::class)
class ZonaParqueoControllerTest {

    @Autowired private lateinit var mockMvc: MockMvc
    @MockitoBean private lateinit var zonaParqueoService: ZonaParqueoService
    @MockitoBean private lateinit var jwtDecoder: JwtDecoder

    private val zonaResponse = ZonaParqueoResponse(id = 1L, nombre = "Zona A", descripcion = "", capacidadMaxima = 10)

    @Test
    fun `GET zonas responde 200 sin token por ser publico`() {
        whenever(zonaParqueoService.getAllZonas()).thenReturn(listOf(zonaResponse))

        mockMvc.perform(get("/api/v1/zonas"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].nombre").value("Zona A"))
            .andExpect(jsonPath("$[0].capacidadMaxima").value(10))
    }

    @Test
    fun `GET estadisticas responde 200 sin token por ser publico`() {
        val estadisticas = EstadisticasZonaResponse(
            zonaId = 1L, zonaNombre = "Zona A", capacidadMaxima = 10,
            disponibles = 7L, ocupados = 3L, total = 10L
        )
        whenever(zonaParqueoService.getEstadisticas(1L)).thenReturn(estadisticas)

        mockMvc.perform(get("/api/v1/zonas/1/estadisticas"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.disponibles").value(7))
            .andExpect(jsonPath("$.ocupados").value(3))
    }

    @Test
    fun `GET estadisticas responde 404 cuando zona no existe`() {
        whenever(zonaParqueoService.getEstadisticas(99L))
            .thenAnswer { throw ZonaParqueoNotFoundException("Zona 99 no encontrada") }

        mockMvc.perform(get("/api/v1/zonas/99/estadisticas"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `POST zonas sin token responde 401`() {
        mockMvc.perform(
            post("/api/v1/zonas")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"nombre":"Zona B","capacidadMaxima":5}""")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `POST zonas con rol DRIVER responde 403`() {
        mockMvc.perform(
            post("/api/v1/zonas")
                .with(jwt().authorities(SimpleGrantedAuthority("ROLE_DRIVER")))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"nombre":"Zona B","capacidadMaxima":5}""")
        ).andExpect(status().isForbidden)
    }

    @Test
    fun `POST zonas con rol ADMIN responde 201`() {
        whenever(zonaParqueoService.createZona(any())).thenReturn(zonaResponse)

        mockMvc.perform(
            post("/api/v1/zonas")
                .with(jwt().authorities(SimpleGrantedAuthority("ROLE_ADMIN")))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"nombre":"Zona A","capacidadMaxima":10}""")
        ).andExpect(status().isCreated)
            .andExpect(jsonPath("$.nombre").value("Zona A"))
    }

    @Test
    fun `POST zonas con nombre duplicado responde 409`() {
        whenever(zonaParqueoService.createZona(any()))
            .thenAnswer { throw ZonaParqueoNombreDuplicadoException("Ya existe Zona A") }

        mockMvc.perform(
            post("/api/v1/zonas")
                .with(jwt().authorities(SimpleGrantedAuthority("ROLE_ADMIN")))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"nombre":"Zona A","capacidadMaxima":10}""")
        ).andExpect(status().isConflict)
    }

    @Test
    fun `POST zonas con nombre blank responde 400`() {
        whenever(zonaParqueoService.createZona(any()))
            .thenAnswer { throw BlankFieldException("nombre no puede estar vacío") }

        mockMvc.perform(
            post("/api/v1/zonas")
                .with(jwt().authorities(SimpleGrantedAuthority("ROLE_ADMIN")))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"nombre":"","capacidadMaxima":10}""")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `PUT zonas sin token responde 401`() {
        mockMvc.perform(
            put("/api/v1/zonas/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"nombre":"Zona A","capacidadMaxima":10}""")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `PUT zonas con rol ADMIN responde 200`() {
        whenever(zonaParqueoService.updateZona(any(), any())).thenReturn(zonaResponse)

        mockMvc.perform(
            put("/api/v1/zonas/1")
                .with(jwt().authorities(SimpleGrantedAuthority("ROLE_ADMIN")))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"nombre":"Zona A","capacidadMaxima":10}""")
        ).andExpect(status().isOk)
    }

    @Test
    fun `DELETE zonas sin token responde 401`() {
        mockMvc.perform(delete("/api/v1/zonas/1"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `DELETE zonas con rol ADMIN responde 204`() {
        mockMvc.perform(
            delete("/api/v1/zonas/1")
                .with(jwt().authorities(SimpleGrantedAuthority("ROLE_ADMIN")))
        ).andExpect(status().isNoContent)
    }

    @Test
    fun `DELETE zonas responde 404 cuando zona no existe`() {
        whenever(zonaParqueoService.deleteZona(99L))
            .thenAnswer { throw ZonaParqueoNotFoundException("Zona 99 no encontrada") }

        mockMvc.perform(
            delete("/api/v1/zonas/99")
                .with(jwt().authorities(SimpleGrantedAuthority("ROLE_ADMIN")))
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `DELETE zonas responde 409 cuando zona tiene puestos`() {
        whenever(zonaParqueoService.deleteZona(1L))
            .thenAnswer { throw ZonaConPuestosException("Zona tiene puestos") }

        mockMvc.perform(
            delete("/api/v1/zonas/1")
                .with(jwt().authorities(SimpleGrantedAuthority("ROLE_ADMIN")))
        ).andExpect(status().isConflict)
    }
}
