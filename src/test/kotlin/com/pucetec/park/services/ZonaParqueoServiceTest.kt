package com.pucetec.park.services

import com.pucetec.park.dto.CreateZonaParqueoRequest
import com.pucetec.park.dto.UpdateZonaParqueoRequest
import com.pucetec.park.entities.EstadoPuesto
import com.pucetec.park.entities.ZonaParqueo
import com.pucetec.park.exceptions.*
import com.pucetec.park.repositories.PuestoParqueoRepository
import com.pucetec.park.repositories.ZonaParqueoRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class ZonaParqueoServiceTest {

    @Mock private lateinit var zonaParqueoRepository: ZonaParqueoRepository
    @Mock private lateinit var puestoParqueoRepository: PuestoParqueoRepository
    @InjectMocks private lateinit var zonaParqueoService: ZonaParqueoService

    private val zonaGuardada = ZonaParqueo(id = 1L, nombre = "Zona A", descripcion = "Principal", capacidadMaxima = 10)

    @Test
    fun `getAllZonas retorna lista de ZonaParqueoResponse`() {
        whenever(zonaParqueoRepository.findAll()).thenReturn(listOf(zonaGuardada))

        val result = zonaParqueoService.getAllZonas()

        assertEquals(1, result.size)
        assertEquals("Zona A", result[0].nombre)
        assertEquals(10, result[0].capacidadMaxima)
    }

    @Test
    fun `createZona guarda y retorna la zona cuando los datos son validos`() {
        val request = CreateZonaParqueoRequest(nombre = "Zona B", descripcion = "", capacidadMaxima = 5)
        val guardada = ZonaParqueo(id = 2L, nombre = "Zona B", descripcion = "", capacidadMaxima = 5)
        whenever(zonaParqueoRepository.existsByNombre("Zona B")).thenReturn(false)
        whenever(zonaParqueoRepository.save(any())).thenReturn(guardada)

        val result = zonaParqueoService.createZona(request)

        assertEquals("Zona B", result.nombre)
        assertEquals(5, result.capacidadMaxima)
    }

    @Test
    fun `createZona lanza BlankFieldException cuando nombre es blank`() {
        assertThrows<BlankFieldException> {
            zonaParqueoService.createZona(CreateZonaParqueoRequest(nombre = "  ", capacidadMaxima = 5))
        }
    }

    @Test
    fun `createZona lanza InvalidCapacityException cuando capacidadMaxima es menor a 1`() {
        assertThrows<InvalidCapacityException> {
            zonaParqueoService.createZona(CreateZonaParqueoRequest(nombre = "Zona C", capacidadMaxima = 0))
        }
    }

    @Test
    fun `createZona lanza ZonaParqueoNombreDuplicadoException cuando nombre ya existe`() {
        whenever(zonaParqueoRepository.existsByNombre("Zona A")).thenReturn(true)

        assertThrows<ZonaParqueoNombreDuplicadoException> {
            zonaParqueoService.createZona(CreateZonaParqueoRequest(nombre = "Zona A", capacidadMaxima = 5))
        }
    }

    @Test
    fun `updateZona actualiza y retorna la zona cuando los datos son validos`() {
        val request = UpdateZonaParqueoRequest(nombre = "Zona A Mod", descripcion = "Nueva desc", capacidadMaxima = 15)
        whenever(zonaParqueoRepository.findById(1L)).thenReturn(Optional.of(zonaGuardada))
        whenever(zonaParqueoRepository.existsByNombreAndIdNot("Zona A Mod", 1L)).thenReturn(false)
        whenever(zonaParqueoRepository.save(any())).thenReturn(
            ZonaParqueo(id = 1L, nombre = "Zona A Mod", descripcion = "Nueva desc", capacidadMaxima = 15)
        )

        val result = zonaParqueoService.updateZona(1L, request)

        assertEquals("Zona A Mod", result.nombre)
        assertEquals(15, result.capacidadMaxima)
    }

    @Test
    fun `updateZona lanza ZonaParqueoNotFoundException cuando zona no existe`() {
        whenever(zonaParqueoRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows<ZonaParqueoNotFoundException> {
            zonaParqueoService.updateZona(99L, UpdateZonaParqueoRequest(nombre = "X", capacidadMaxima = 5))
        }
    }

    @Test
    fun `updateZona lanza BlankFieldException cuando nombre es blank`() {
        whenever(zonaParqueoRepository.findById(1L)).thenReturn(Optional.of(zonaGuardada))

        assertThrows<BlankFieldException> {
            zonaParqueoService.updateZona(1L, UpdateZonaParqueoRequest(nombre = "", capacidadMaxima = 5))
        }
    }

    @Test
    fun `updateZona lanza InvalidCapacityException cuando capacidadMaxima es menor a 1`() {
        whenever(zonaParqueoRepository.findById(1L)).thenReturn(Optional.of(zonaGuardada))

        assertThrows<InvalidCapacityException> {
            zonaParqueoService.updateZona(1L, UpdateZonaParqueoRequest(nombre = "Zona A", capacidadMaxima = -1))
        }
    }

    @Test
    fun `updateZona lanza ZonaParqueoNombreDuplicadoException cuando nombre ya pertenece a otra zona`() {
        whenever(zonaParqueoRepository.findById(1L)).thenReturn(Optional.of(zonaGuardada))
        whenever(zonaParqueoRepository.existsByNombreAndIdNot("Zona B", 1L)).thenReturn(true)

        assertThrows<ZonaParqueoNombreDuplicadoException> {
            zonaParqueoService.updateZona(1L, UpdateZonaParqueoRequest(nombre = "Zona B", capacidadMaxima = 5))
        }
    }

    @Test
    fun `deleteZona elimina la zona cuando no tiene puestos`() {
        whenever(zonaParqueoRepository.findById(1L)).thenReturn(Optional.of(zonaGuardada))
        whenever(puestoParqueoRepository.existsByZonaId(1L)).thenReturn(false)

        zonaParqueoService.deleteZona(1L)
    }

    @Test
    fun `deleteZona lanza ZonaParqueoNotFoundException cuando zona no existe`() {
        whenever(zonaParqueoRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows<ZonaParqueoNotFoundException> {
            zonaParqueoService.deleteZona(99L)
        }
    }

    @Test
    fun `deleteZona lanza ZonaConPuestosException cuando zona tiene puestos asignados`() {
        whenever(zonaParqueoRepository.findById(1L)).thenReturn(Optional.of(zonaGuardada))
        whenever(puestoParqueoRepository.existsByZonaId(1L)).thenReturn(true)

        assertThrows<ZonaConPuestosException> {
            zonaParqueoService.deleteZona(1L)
        }
    }

    @Test
    fun `getEstadisticas retorna conteo correcto de puestos por estado`() {
        whenever(zonaParqueoRepository.findById(1L)).thenReturn(Optional.of(zonaGuardada))
        whenever(puestoParqueoRepository.countByZonaIdAndEstado(1L, EstadoPuesto.DISPONIBLE)).thenReturn(7L)
        whenever(puestoParqueoRepository.countByZonaIdAndEstado(1L, EstadoPuesto.OCUPADO)).thenReturn(3L)

        val result = zonaParqueoService.getEstadisticas(1L)

        assertEquals(1L, result.zonaId)
        assertEquals("Zona A", result.zonaNombre)
        assertEquals(10, result.capacidadMaxima)
        assertEquals(7L, result.disponibles)
        assertEquals(3L, result.ocupados)
        assertEquals(10L, result.total)
    }

    @Test
    fun `getEstadisticas lanza ZonaParqueoNotFoundException cuando zona no existe`() {
        whenever(zonaParqueoRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows<ZonaParqueoNotFoundException> {
            zonaParqueoService.getEstadisticas(99L)
        }
    }
}
