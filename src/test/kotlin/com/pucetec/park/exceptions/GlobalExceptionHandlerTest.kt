package com.pucetec.park.exceptions

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class GlobalExceptionHandlerTest {

    private val handler = GlobalExceptionHandler()

    @Test
    fun `ZonaParqueoNotFoundException sin mensaje responde 404 con mensaje por defecto`() {
        val response = handler.handleZonaParqueoNotFound(ZonaParqueoNotFoundException())
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals("Zona de parqueo no encontrada", response.body?.message)
    }

    @Test
    fun `ZonaParqueoNotFoundException con mensaje responde 404 con el mensaje`() {
        val response = handler.handleZonaParqueoNotFound(ZonaParqueoNotFoundException("Zona 5 no encontrada"))
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals("Zona 5 no encontrada", response.body?.message)
    }

    @Test
    fun `PuestoParqueoNotFoundException sin mensaje responde 404 con mensaje por defecto`() {
        val response = handler.handlePuestoParqueoNotFound(PuestoParqueoNotFoundException())
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals("Puesto de parqueo no encontrado", response.body?.message)
    }

    @Test
    fun `PuestoParqueoNotFoundException con mensaje responde 404 con el mensaje`() {
        val response = handler.handlePuestoParqueoNotFound(PuestoParqueoNotFoundException("Puesto 3 no encontrado"))
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals("Puesto 3 no encontrado", response.body?.message)
    }

    @Test
    fun `HistorialParqueoNotFoundException sin mensaje responde 404 con mensaje por defecto`() {
        val response = handler.handleHistorialParqueoNotFound(HistorialParqueoNotFoundException())
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals("Historial de parqueo no encontrado", response.body?.message)
    }

    @Test
    fun `SlotAlreadyOccupiedException sin mensaje responde 409 con mensaje por defecto`() {
        val response = handler.handleSlotAlreadyOccupied(SlotAlreadyOccupiedException())
        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals("El puesto ya está ocupado", response.body?.message)
    }

    @Test
    fun `SlotAlreadyAvailableException sin mensaje responde 409 con mensaje por defecto`() {
        val response = handler.handleSlotAlreadyAvailable(SlotAlreadyAvailableException())
        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals("El puesto ya está disponible", response.body?.message)
    }

    @Test
    fun `UnauthorizedAccessException sin mensaje responde 403 con mensaje por defecto`() {
        val response = handler.handleUnauthorizedAccess(UnauthorizedAccessException())
        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
        assertEquals("Acceso no autorizado", response.body?.message)
    }

    @Test
    fun `ZonaParqueoNombreDuplicadoException sin mensaje responde 409 con mensaje por defecto`() {
        val response = handler.handleZonaParqueoNombreDuplicado(ZonaParqueoNombreDuplicadoException())
        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals("Ya existe una zona con ese nombre", response.body?.message)
    }

    @Test
    fun `NumeroPuestoDuplicadoException sin mensaje responde 409 con mensaje por defecto`() {
        val response = handler.handleNumeroPuestoDuplicado(NumeroPuestoDuplicadoException())
        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals("Ya existe ese número de puesto en la zona", response.body?.message)
    }

    @Test
    fun `ZonaParqueoLlenaException sin mensaje responde 409 con mensaje por defecto`() {
        val response = handler.handleZonaParqueoLlena(ZonaParqueoLlenaException())
        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals("La zona de parqueo está llena", response.body?.message)
    }

    @Test
    fun `ZonaConPuestosException sin mensaje responde 409 con mensaje por defecto`() {
        val response = handler.handleZonaConPuestos(ZonaConPuestosException())
        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals("La zona tiene puestos asignados", response.body?.message)
    }

    @Test
    fun `BlankFieldException responde 400 con el mensaje`() {
        val response = handler.handleBlankField(BlankFieldException("nombre no puede estar vacío"))
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("nombre no puede estar vacío", response.body?.message)
    }

    @Test
    fun `InvalidCapacityException responde 400 con el mensaje`() {
        val response = handler.handleInvalidCapacity(InvalidCapacityException("capacidadMaxima debe ser al menos 1"))
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("capacidadMaxima debe ser al menos 1", response.body?.message)
    }
}
