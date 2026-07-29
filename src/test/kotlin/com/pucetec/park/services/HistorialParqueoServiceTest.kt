package com.pucetec.park.services

import com.pucetec.park.entities.*
import com.pucetec.park.exceptions.PuestoParqueoNotFoundException
import com.pucetec.park.repositories.HistorialParqueoRepository
import com.pucetec.park.repositories.PuestoParqueoRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class HistorialParqueoServiceTest {

    @Mock private lateinit var historialParqueoRepository: HistorialParqueoRepository
    @Mock private lateinit var puestoParqueoRepository: PuestoParqueoRepository
    @InjectMocks private lateinit var historialParqueoService: HistorialParqueoService

    private val zona = ZonaParqueo(id = 1L, nombre = "Zona A", capacidadMaxima = 10)
    private val puesto = PuestoParqueo(id = 1L, zona = zona, numeroPuesto = "A01", estado = EstadoPuesto.DISPONIBLE)
    private val historial = HistorialParqueo(id = 1L, puesto = puesto, username = "jdoe")

    @Test
    fun `getHistorialByUsername retorna lista ordenada por fechaIngreso del usuario`() {
        whenever(historialParqueoRepository.findByUsernameOrderByFechaIngresoDesc("jdoe"))
            .thenReturn(listOf(historial))

        val result = historialParqueoService.getHistorialByUsername("jdoe")

        assertEquals(1, result.size)
        assertEquals("jdoe", result[0].username)
    }

    @Test
    fun `getHistorialByPuesto retorna lista del historial cuando el puesto existe`() {
        whenever(puestoParqueoRepository.existsById(1L)).thenReturn(true)
        whenever(historialParqueoRepository.findByPuestoIdOrderByFechaIngresoDesc(1L))
            .thenReturn(listOf(historial))

        val result = historialParqueoService.getHistorialByPuesto(1L)

        assertEquals(1, result.size)
        assertEquals("A01", result[0].puesto.numeroPuesto)
    }

    @Test
    fun `getHistorialByPuesto lanza PuestoParqueoNotFoundException cuando el puesto no existe`() {
        whenever(puestoParqueoRepository.existsById(99L)).thenReturn(false)

        assertThrows<PuestoParqueoNotFoundException> {
            historialParqueoService.getHistorialByPuesto(99L)
        }
    }
}
