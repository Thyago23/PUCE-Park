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
        assertEquals("Zona A", result[0].name)
        assertEquals(10, result[0].maxCapacity)
    }

    @Test
    fun `createZona guarda y retorna la zona cuando los datos son validos`() {
        val request = CreateZonaParqueoRequest(name = "Zona B", description = "", maxCapacity = 5)
        val guardada = ZonaParqueo(id = 2L, nombre = "Zona B", descripcion = "", capacidadMaxima = 5)
        whenever(zonaParqueoRepository.existsByNombre("Zona B")).thenReturn(false)
        whenever(zonaParqueoRepository.save(any())).thenReturn(guardada)

        val result = zonaParqueoService.createZona(request)

        assertEquals("Zona B", result.name)
        assertEquals(5, result.maxCapacity)
    }

    @Test
    fun `createZona lanza BlankFieldException cuando name es blank`() {
        assertThrows<BlankFieldException> {
            zonaParqueoService.createZona(CreateZonaParqueoRequest(name = "  ", maxCapacity = 5))
        }
    }

    @Test
    fun `createZona lanza InvalidCapacityException cuando maxCapacity es menor a 1`() {
        assertThrows<InvalidCapacityException> {
            zonaParqueoService.createZona(CreateZonaParqueoRequest(name = "Zona C", maxCapacity = 0))
        }
    }

    @Test
    fun `createZona lanza ZonaParqueoNombreDuplicadoException cuando name ya existe`() {
        whenever(zonaParqueoRepository.existsByNombre("Zona A")).thenReturn(true)

        assertThrows<ZonaParqueoNombreDuplicadoException> {
            zonaParqueoService.createZona(CreateZonaParqueoRequest(name = "Zona A", maxCapacity = 5))
        }
    }

    @Test
    fun `updateZona actualiza y retorna la zona cuando los datos son validos`() {
        val request = UpdateZonaParqueoRequest(name = "Zona A Mod", description = "Nueva desc", maxCapacity = 15)
        whenever(zonaParqueoRepository.findById(1L)).thenReturn(Optional.of(zonaGuardada))
        whenever(zonaParqueoRepository.existsByNombreAndIdNot("Zona A Mod", 1L)).thenReturn(false)
        whenever(zonaParqueoRepository.save(any())).thenReturn(
            ZonaParqueo(id = 1L, nombre = "Zona A Mod", descripcion = "Nueva desc", capacidadMaxima = 15)
        )

        val result = zonaParqueoService.updateZona(1L, request)

        assertEquals("Zona A Mod", result.name)
        assertEquals(15, result.maxCapacity)
    }

    @Test
    fun `updateZona lanza ZonaParqueoNotFoundException cuando zona no existe`() {
        whenever(zonaParqueoRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows<ZonaParqueoNotFoundException> {
            zonaParqueoService.updateZona(99L, UpdateZonaParqueoRequest(name = "X", maxCapacity = 5))
        }
    }

    @Test
    fun `updateZona lanza BlankFieldException cuando name es blank`() {
        whenever(zonaParqueoRepository.findById(1L)).thenReturn(Optional.of(zonaGuardada))

        assertThrows<BlankFieldException> {
            zonaParqueoService.updateZona(1L, UpdateZonaParqueoRequest(name = "", maxCapacity = 5))
        }
    }

    @Test
    fun `updateZona lanza InvalidCapacityException cuando maxCapacity es menor a 1`() {
        whenever(zonaParqueoRepository.findById(1L)).thenReturn(Optional.of(zonaGuardada))

        assertThrows<InvalidCapacityException> {
            zonaParqueoService.updateZona(1L, UpdateZonaParqueoRequest(name = "Zona A", maxCapacity = -1))
        }
    }

    @Test
    fun `updateZona lanza ZonaParqueoNombreDuplicadoException cuando name ya pertenece a otra zona`() {
        whenever(zonaParqueoRepository.findById(1L)).thenReturn(Optional.of(zonaGuardada))
        whenever(zonaParqueoRepository.existsByNombreAndIdNot("Zona B", 1L)).thenReturn(true)

        assertThrows<ZonaParqueoNombreDuplicadoException> {
            zonaParqueoService.updateZona(1L, UpdateZonaParqueoRequest(name = "Zona B", maxCapacity = 5))
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

        assertEquals(1L, result.zoneId)
        assertEquals("Zona A", result.zoneName)
        assertEquals(10, result.maxCapacity)
        assertEquals(7L, result.available)
        assertEquals(3L, result.occupied)
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
