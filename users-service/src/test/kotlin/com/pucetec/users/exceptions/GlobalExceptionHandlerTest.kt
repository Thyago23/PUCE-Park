package com.pucetec.users.exceptions

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class GlobalExceptionHandlerTest {

    private val handler = GlobalExceptionHandler()

    @Test
    fun `UserProfileNotFoundException sin mensaje responde 404 con mensaje por defecto`() {
        val res = handler.handleUserProfileNotFound(UserProfileNotFoundException())
        assertEquals(HttpStatus.NOT_FOUND, res.statusCode)
        assertEquals("User profile not found", res.body?.message)
    }

    @Test
    fun `UserProfileNotFoundException con mensaje responde 404 con ese mensaje`() {
        val res = handler.handleUserProfileNotFound(UserProfileNotFoundException("no existe"))
        assertEquals(HttpStatus.NOT_FOUND, res.statusCode)
        assertEquals("no existe", res.body?.message)
    }

    @Test
    fun `UserProfileAlreadyExistsException sin mensaje responde 409 con mensaje por defecto`() {
        val res = handler.handleUserProfileAlreadyExists(UserProfileAlreadyExistsException())
        assertEquals(HttpStatus.CONFLICT, res.statusCode)
        assertEquals("User profile already exists", res.body?.message)
    }
}
