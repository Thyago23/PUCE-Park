package com.pucetec.park.controllers

import com.pucetec.park.config.SecurityConfig
import com.pucetec.park.dto.PuestoParqueoResponse
import com.pucetec.park.dto.ZonaParqueoResponse
import com.pucetec.park.entities.EstadoPuesto
import com.pucetec.park.exceptions.*
import com.pucetec.park.services.PuestoParqueoService
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

@WebMvcTest(PuestoParqueoController::class)
@Import(SecurityConfig::class, GlobalExceptionHandler::class)
class PuestoParqueoControllerTest {

    @Autowired private lateinit var mockMvc: MockMvc
    @MockitoBean private lateinit var puestoParqueoService: PuestoParqueoService
    @MockitoBean private lateinit var jwtDecoder: JwtDecoder

    private val zonaResponse = ZonaParqueoResponse(id = 1L, nombre = "Zona A", descripcion = "", capacidadMaxima = 10)
    private val puestoResponse = PuestoParqueoResponse(id = 1L, numeroPuesto = "A01", estado = EstadoPuesto.DISPONIBLE, zona = zonaResponse)
    private val puestoOcupadoResponse = puestoResponse.copy(estado = EstadoPuesto.OCUPADO)

    @Test
    fun `GET puestos por zona responde 200 sin token por ser publico`() {
        whenever(puestoParqueoService.getPuestosByZona(1L)).thenReturn(listOf(puestoResponse))

        mockMvc.perform(get("/api/v1/puestos/zona/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].numeroPuesto").value("A01"))
            .andExpect(jsonPath("$[0].estado").value("DISPONIBLE"))
    }

    @Test
    fun `POST puestos sin token responde 401`() {
        mockMvc.perform(
            post("/api/v1/puestos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"zonaId":1,"numeroPuesto":"A01"}""")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `POST puestos con rol DRIVER responde 403`() {
        mockMvc.perform(
            post("/api/v1/puestos")
                .with(jwt().authorities(SimpleGrantedAuthority("ROLE_DRIVER")))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"zonaId":1,"numeroPuesto":"A01"}""")
        ).andExpect(status().isForbidden)
    }

    @Test
    fun `POST puestos con rol ADMIN responde 201`() {
        whenever(puestoParqueoService.createPuesto(any())).thenReturn(puestoResponse)

        mockMvc.perform(
            post("/api/v1/puestos")
                .with(jwt().authorities(SimpleGrantedAuthority("ROLE_ADMIN")))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"zonaId":1,"numeroPuesto":"A01"}""")
        ).andExpect(status().isCreated)
            .andExpect(jsonPath("$.numeroPuesto").value("A01"))
    }

    @Test
    fun `POST puestos responde 409 cuando la zona esta llena`() {
        whenever(puestoParqueoService.createPuesto(any()))
            .thenAnswer { throw ZonaParqueoLlenaException("Zona llena") }

        mockMvc.perform(
            post("/api/v1/puestos")
                .with(jwt().authorities(SimpleGrantedAuthority("ROLE_ADMIN")))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"zonaId":1,"numeroPuesto":"A11"}""")
        ).andExpect(status().isConflict)
    }

    @Test
    fun `PUT puestos update sin token responde 401`() {
        mockMvc.perform(
            put("/api/v1/puestos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"numeroPuesto":"A01-MOD"}""")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `PUT puestos update con rol DRIVER responde 403`() {
        mockMvc.perform(
            put("/api/v1/puestos/1")
                .with(jwt().authorities(SimpleGrantedAuthority("ROLE_DRIVER")))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"numeroPuesto":"A01-MOD"}""")
        ).andExpect(status().isForbidden)
    }

    @Test
    fun `PUT puestos update con rol ADMIN responde 200`() {
        whenever(puestoParqueoService.updatePuesto(any(), any())).thenReturn(puestoResponse)

        mockMvc.perform(
            put("/api/v1/puestos/1")
                .with(jwt().authorities(SimpleGrantedAuthority("ROLE_ADMIN")))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"numeroPuesto":"A01-MOD"}""")
        ).andExpect(status().isOk)
    }

    @Test
    fun `PUT ocupar sin token responde 401`() {
        mockMvc.perform(put("/api/v1/puestos/1/ocupar"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `PUT ocupar con rol ADMIN responde 403`() {
        mockMvc.perform(
            put("/api/v1/puestos/1/ocupar")
                .with(jwt().authorities(SimpleGrantedAuthority("ROLE_ADMIN")))
        ).andExpect(status().isForbidden)
    }

    @Test
    fun `PUT ocupar con rol DRIVER responde 200`() {
        whenever(puestoParqueoService.ocuparPuesto(any(), any())).thenReturn(puestoOcupadoResponse)

        mockMvc.perform(
            put("/api/v1/puestos/1/ocupar")
                .with(jwt().authorities(SimpleGrantedAuthority("ROLE_DRIVER")))
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.estado").value("OCUPADO"))
    }

    @Test
    fun `PUT ocupar responde 409 cuando puesto ya esta ocupado`() {
        whenever(puestoParqueoService.ocuparPuesto(any(), any()))
            .thenAnswer { throw SlotAlreadyOccupiedException("Puesto ya ocupado") }

        mockMvc.perform(
            put("/api/v1/puestos/1/ocupar")
                .with(jwt().authorities(SimpleGrantedAuthority("ROLE_DRIVER")))
        ).andExpect(status().isConflict)
    }

    @Test
    fun `PUT liberar sin token responde 401`() {
        mockMvc.perform(put("/api/v1/puestos/1/liberar"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `PUT liberar con rol DRIVER responde 200`() {
        whenever(puestoParqueoService.liberarPuesto(any(), any(), any())).thenReturn(puestoResponse)

        mockMvc.perform(
            put("/api/v1/puestos/1/liberar")
                .with(jwt().authorities(SimpleGrantedAuthority("ROLE_DRIVER")))
        ).andExpect(status().isOk)
    }

    @Test
    fun `PUT liberar responde 409 cuando puesto ya esta disponible`() {
        whenever(puestoParqueoService.liberarPuesto(any(), any(), any()))
            .thenAnswer { throw SlotAlreadyAvailableException("Puesto ya disponible") }

        mockMvc.perform(
            put("/api/v1/puestos/1/liberar")
                .with(jwt().authorities(SimpleGrantedAuthority("ROLE_DRIVER")))
        ).andExpect(status().isConflict)
    }

    @Test
    fun `PUT forzar-liberacion sin token responde 401`() {
        mockMvc.perform(put("/api/v1/puestos/1/forzar-liberacion"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `PUT forzar-liberacion con rol DRIVER responde 403`() {
        mockMvc.perform(
            put("/api/v1/puestos/1/forzar-liberacion")
                .with(jwt().authorities(SimpleGrantedAuthority("ROLE_DRIVER")))
        ).andExpect(status().isForbidden)
    }

    @Test
    fun `PUT forzar-liberacion con rol GUARD responde 200`() {
        whenever(puestoParqueoService.liberarPuesto(any(), any(), any())).thenReturn(puestoResponse)

        mockMvc.perform(
            put("/api/v1/puestos/1/forzar-liberacion")
                .with(jwt().authorities(SimpleGrantedAuthority("ROLE_GUARD")))
        ).andExpect(status().isOk)
    }
}
