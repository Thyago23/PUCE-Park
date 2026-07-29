package com.pucetec.park.services

import com.pucetec.park.dto.UpdatePerfilUsuarioRequest
import com.pucetec.park.entities.PerfilUsuario
import com.pucetec.park.repositories.PerfilUsuarioRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class PerfilUsuarioServiceTest {

    @Mock private lateinit var perfilUsuarioRepository: PerfilUsuarioRepository
    @InjectMocks private lateinit var perfilUsuarioService: PerfilUsuarioService

    private val perfilExistente = PerfilUsuario(
        id = 1L, username = "jdoe", nombreCompleto = "John Doe",
        placaVehiculo = "ABC-123", modoOscuro = false
    )

    @Test
    fun `getOrCreatePerfil retorna el perfil existente cuando el usuario ya tiene uno`() {
        whenever(perfilUsuarioRepository.findByUsername("jdoe")).thenReturn(Optional.of(perfilExistente))

        val result = perfilUsuarioService.getOrCreatePerfil("jdoe")

        assertEquals("jdoe", result.username)
        assertEquals("John Doe", result.nombreCompleto)
    }

    @Test
    fun `getOrCreatePerfil crea y retorna un nuevo perfil cuando el usuario no tiene uno`() {
        val nuevoPerfil = PerfilUsuario(id = 2L, username = "nuevo_user")
        whenever(perfilUsuarioRepository.findByUsername("nuevo_user")).thenReturn(Optional.empty())
        whenever(perfilUsuarioRepository.save(any())).thenReturn(nuevoPerfil)

        val result = perfilUsuarioService.getOrCreatePerfil("nuevo_user")

        assertEquals("nuevo_user", result.username)
    }

    @Test
    fun `updatePerfil actualiza y retorna el perfil con los nuevos datos`() {
        val request = UpdatePerfilUsuarioRequest(
            nombreCompleto = "John Updated", placaVehiculo = "XYZ-999", modoOscuro = true
        )
        val actualizado = PerfilUsuario(
            id = 1L, username = "jdoe",
            nombreCompleto = "John Updated", placaVehiculo = "XYZ-999", modoOscuro = true
        )
        whenever(perfilUsuarioRepository.findByUsername("jdoe")).thenReturn(Optional.of(perfilExistente))
        whenever(perfilUsuarioRepository.save(any())).thenReturn(actualizado)

        val result = perfilUsuarioService.updatePerfil("jdoe", request)

        assertEquals("John Updated", result.nombreCompleto)
        assertEquals("XYZ-999", result.placaVehiculo)
        assertTrue(result.modoOscuro)
    }
}
