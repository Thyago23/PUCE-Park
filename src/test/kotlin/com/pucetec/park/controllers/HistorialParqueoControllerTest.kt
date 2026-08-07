package com.pucetec.park.controllers

import com.pucetec.park.config.SecurityConfig
import com.pucetec.park.dto.HistorialParqueoResponse
import com.pucetec.park.dto.PuestoParqueoResponse
import com.pucetec.park.dto.ZonaParqueoResponse
import com.pucetec.park.entities.EstadoPuesto
import com.pucetec.park.exceptions.GlobalExceptionHandler
import com.pucetec.park.exceptions.PuestoParqueoNotFoundException
import com.pucetec.park.services.HistorialParqueoService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDateTime

@WebMvcTest(HistorialParqueoController::class)
@Import(SecurityConfig::class, GlobalExceptionHandler::class)
class HistorialParqueoControllerTest {

    @Autowired private lateinit var mockMvc: MockMvc
    @MockitoBean private lateinit var historialParqueoService: HistorialParqueoService
    @MockitoBean private lateinit var jwtDecoder: JwtDecoder

    private val zonaResponse = ZonaParqueoResponse(
        id = 1L, name = "Zona A", description = "", location = "", maxCapacity = 10,
        availableSpaces = 9, occupiedSpaces = 1, totalSpaces = 10
    )
    private val puestoResponse = PuestoParqueoResponse(
        id = 1L, spaceNumber = "A01", row = "A", order = 1,
        status = EstadoPuesto.DISPONIBLE, zone = zonaResponse
    )
    private val historialResponse = HistorialParqueoResponse(
        id = 1L, ticketCode = "PARK-ABC123", username = "jdoe",
        entryDate = LocalDateTime.of(2026, 7, 1, 10, 0),
        exitDate = null, vehiclePlate = null, space = puestoResponse
    )

    @Test
    fun `GET historial me sin token responde 401`() {
        mockMvc.perform(get("/api/v1/historial/me"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `GET historial me con rol GUARD responde 403`() {
        mockMvc.perform(
            get("/api/v1/historial/me")
                .with(jwt().authorities(SimpleGrantedAuthority("ROLE_GUARD")))
        ).andExpect(status().isForbidden)
    }

    @Test
    fun `GET historial me con rol USER responde 200`() {
        whenever(historialParqueoService.getHistorialByUsername(any())).thenReturn(listOf(historialResponse))

        mockMvc.perform(
            get("/api/v1/historial/me")
                .with(jwt().authorities(SimpleGrantedAuthority("ROLE_USER")))
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$[0].username").value("jdoe"))
    }

    @Test
    fun `GET historial puesto sin token responde 401`() {
        mockMvc.perform(get("/api/v1/historial/puesto/1"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `GET historial puesto con rol USER responde 403`() {
        mockMvc.perform(
            get("/api/v1/historial/puesto/1")
                .with(jwt().authorities(SimpleGrantedAuthority("ROLE_USER")))
        ).andExpect(status().isForbidden)
    }

    @Test
    fun `GET historial puesto con rol GUARD responde 200`() {
        whenever(historialParqueoService.getHistorialByPuesto(1L)).thenReturn(listOf(historialResponse))

        mockMvc.perform(
            get("/api/v1/historial/puesto/1")
                .with(jwt().authorities(SimpleGrantedAuthority("ROLE_GUARD")))
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$[0].space.spaceNumber").value("A01"))
    }

    @Test
    fun `GET historial puesto con rol ADMIN responde 200`() {
        whenever(historialParqueoService.getHistorialByPuesto(1L)).thenReturn(listOf(historialResponse))

        mockMvc.perform(
            get("/api/v1/historial/puesto/1")
                .with(jwt().authorities(SimpleGrantedAuthority("ROLE_ADMIN")))
        ).andExpect(status().isOk)
    }

    @Test
    fun `GET historial puesto responde 404 cuando el puesto no existe`() {
        whenever(historialParqueoService.getHistorialByPuesto(99L))
            .thenAnswer { throw PuestoParqueoNotFoundException("Puesto 99 no encontrado") }

        mockMvc.perform(
            get("/api/v1/historial/puesto/99")
                .with(jwt().authorities(SimpleGrantedAuthority("ROLE_GUARD")))
        ).andExpect(status().isNotFound)
    }
}
