package com.pucetec.park.services

import com.pucetec.park.dto.CreatePuestoParqueoRequest
import com.pucetec.park.dto.UpdatePuestoParqueoRequest
import com.pucetec.park.entities.*
import com.pucetec.park.exceptions.*
import com.pucetec.park.repositories.HistorialParqueoRepository
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
class PuestoParqueoServiceTest {

    @Mock private lateinit var puestoParqueoRepository: PuestoParqueoRepository
    @Mock private lateinit var zonaParqueoRepository: ZonaParqueoRepository
    @Mock private lateinit var historialParqueoRepository: HistorialParqueoRepository
    @InjectMocks private lateinit var puestoParqueoService: PuestoParqueoService

    private val zona = ZonaParqueo(id = 1L, nombre = "Zona A", descripcion = "", capacidadMaxima = 10)
    private val puestoDisponible = PuestoParqueo(id = 1L, zona = zona, numeroPuesto = "A01", estado = EstadoPuesto.DISPONIBLE)
    private val puestoOcupado = PuestoParqueo(id = 2L, zona = zona, numeroPuesto = "A02", estado = EstadoPuesto.OCUPADO)

    @Test
    fun `getPuestosByZona retorna lista de PuestoParqueoResponse`() {
        whenever(puestoParqueoRepository.findByZonaId(1L)).thenReturn(listOf(puestoDisponible))

        val result = puestoParqueoService.getPuestosByZona(1L)

        assertEquals(1, result.size)
        assertEquals("A01", result[0].numeroPuesto)
        assertEquals(EstadoPuesto.DISPONIBLE, result[0].estado)
    }

    @Test
    fun `createPuesto guarda y retorna el puesto cuando los datos son validos`() {
        val request = CreatePuestoParqueoRequest(zonaId = 1L, numeroPuesto = "A01")
        whenever(zonaParqueoRepository.findById(1L)).thenReturn(Optional.of(zona))
        whenever(puestoParqueoRepository.existsByNumeroPuestoAndZonaId("A01", 1L)).thenReturn(false)
        whenever(puestoParqueoRepository.countByZonaId(1L)).thenReturn(3L)
        whenever(puestoParqueoRepository.save(any())).thenReturn(puestoDisponible)

        val result = puestoParqueoService.createPuesto(request)

        assertEquals("A01", result.numeroPuesto)
        assertEquals(EstadoPuesto.DISPONIBLE, result.estado)
    }

    @Test
    fun `createPuesto lanza BlankFieldException cuando numeroPuesto es blank`() {
        assertThrows<BlankFieldException> {
            puestoParqueoService.createPuesto(CreatePuestoParqueoRequest(zonaId = 1L, numeroPuesto = ""))
        }
    }

    @Test
    fun `createPuesto lanza ZonaParqueoNotFoundException cuando zona no existe`() {
        whenever(zonaParqueoRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows<ZonaParqueoNotFoundException> {
            puestoParqueoService.createPuesto(CreatePuestoParqueoRequest(zonaId = 99L, numeroPuesto = "A01"))
        }
    }

    @Test
    fun `createPuesto lanza NumeroPuestoDuplicadoException cuando numeroPuesto ya existe en la zona`() {
        whenever(zonaParqueoRepository.findById(1L)).thenReturn(Optional.of(zona))
        whenever(puestoParqueoRepository.existsByNumeroPuestoAndZonaId("A01", 1L)).thenReturn(true)

        assertThrows<NumeroPuestoDuplicadoException> {
            puestoParqueoService.createPuesto(CreatePuestoParqueoRequest(zonaId = 1L, numeroPuesto = "A01"))
        }
    }

    @Test
    fun `createPuesto lanza ZonaParqueoLlenaException cuando zona ha alcanzado su capacidad`() {
        whenever(zonaParqueoRepository.findById(1L)).thenReturn(Optional.of(zona))
        whenever(puestoParqueoRepository.existsByNumeroPuestoAndZonaId("A11", 1L)).thenReturn(false)
        whenever(puestoParqueoRepository.countByZonaId(1L)).thenReturn(10L)

        assertThrows<ZonaParqueoLlenaException> {
            puestoParqueoService.createPuesto(CreatePuestoParqueoRequest(zonaId = 1L, numeroPuesto = "A11"))
        }
    }

    @Test
    fun `updatePuesto actualiza el numeroPuesto cuando los datos son validos`() {
        val request = UpdatePuestoParqueoRequest(numeroPuesto = "A01-MOD")
        whenever(puestoParqueoRepository.findById(1L)).thenReturn(Optional.of(puestoDisponible))
        whenever(puestoParqueoRepository.existsByNumeroPuestoAndZonaIdAndIdNot("A01-MOD", 1L, 1L)).thenReturn(false)
        whenever(puestoParqueoRepository.save(any())).thenReturn(
            PuestoParqueo(id = 1L, zona = zona, numeroPuesto = "A01-MOD", estado = EstadoPuesto.DISPONIBLE)
        )

        val result = puestoParqueoService.updatePuesto(1L, request)

        assertEquals("A01-MOD", result.numeroPuesto)
    }

    @Test
    fun `updatePuesto lanza PuestoParqueoNotFoundException cuando puesto no existe`() {
        whenever(puestoParqueoRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows<PuestoParqueoNotFoundException> {
            puestoParqueoService.updatePuesto(99L, UpdatePuestoParqueoRequest(numeroPuesto = "X"))
        }
    }

    @Test
    fun `updatePuesto lanza BlankFieldException cuando numeroPuesto es blank`() {
        whenever(puestoParqueoRepository.findById(1L)).thenReturn(Optional.of(puestoDisponible))

        assertThrows<BlankFieldException> {
            puestoParqueoService.updatePuesto(1L, UpdatePuestoParqueoRequest(numeroPuesto = "   "))
        }
    }

    @Test
    fun `updatePuesto lanza NumeroPuestoDuplicadoException cuando numeroPuesto ya existe en la zona`() {
        whenever(puestoParqueoRepository.findById(1L)).thenReturn(Optional.of(puestoDisponible))
        whenever(puestoParqueoRepository.existsByNumeroPuestoAndZonaIdAndIdNot("A02", 1L, 1L)).thenReturn(true)

        assertThrows<NumeroPuestoDuplicadoException> {
            puestoParqueoService.updatePuesto(1L, UpdatePuestoParqueoRequest(numeroPuesto = "A02"))
        }
    }

    @Test
    fun `ocuparPuesto marca el puesto como OCUPADO y registra historial`() {
        whenever(puestoParqueoRepository.findByIdWithPessimisticLock(1L)).thenReturn(Optional.of(puestoDisponible))
        whenever(puestoParqueoRepository.save(any())).thenReturn(puestoDisponible)
        whenever(historialParqueoRepository.save(any())).thenReturn(
            HistorialParqueo(puesto = puestoDisponible, username = "jdoe")
        )

        val result = puestoParqueoService.ocuparPuesto(1L, "jdoe")

        assertEquals(EstadoPuesto.OCUPADO, result.estado)
    }

    @Test
    fun `ocuparPuesto lanza PuestoParqueoNotFoundException cuando puesto no existe`() {
        whenever(puestoParqueoRepository.findByIdWithPessimisticLock(99L)).thenReturn(Optional.empty())

        assertThrows<PuestoParqueoNotFoundException> {
            puestoParqueoService.ocuparPuesto(99L, "jdoe")
        }
    }

    @Test
    fun `ocuparPuesto lanza SlotAlreadyOccupiedException cuando puesto ya esta ocupado`() {
        whenever(puestoParqueoRepository.findByIdWithPessimisticLock(2L)).thenReturn(Optional.of(puestoOcupado))

        assertThrows<SlotAlreadyOccupiedException> {
            puestoParqueoService.ocuparPuesto(2L, "jdoe")
        }
    }

    @Test
    fun `liberarPuesto marca el puesto como DISPONIBLE cuando el usuario es el propietario`() {
        val historial = HistorialParqueo(id = 1L, puesto = puestoOcupado, username = "jdoe")
        whenever(puestoParqueoRepository.findByIdWithPessimisticLock(2L)).thenReturn(Optional.of(puestoOcupado))
        whenever(historialParqueoRepository.findFirstByPuestoIdAndFechaSalidaIsNullOrderByFechaIngresoDesc(2L))
            .thenReturn(Optional.of(historial))
        whenever(historialParqueoRepository.save(any())).thenReturn(historial)
        whenever(puestoParqueoRepository.save(any())).thenReturn(puestoOcupado)

        val result = puestoParqueoService.liberarPuesto(2L, "jdoe", isGuard = false)

        assertEquals(EstadoPuesto.DISPONIBLE, result.estado)
    }

    @Test
    fun `liberarPuesto lanza PuestoParqueoNotFoundException cuando puesto no existe`() {
        whenever(puestoParqueoRepository.findByIdWithPessimisticLock(99L)).thenReturn(Optional.empty())

        assertThrows<PuestoParqueoNotFoundException> {
            puestoParqueoService.liberarPuesto(99L, "jdoe", isGuard = false)
        }
    }

    @Test
    fun `liberarPuesto lanza SlotAlreadyAvailableException cuando puesto ya esta disponible`() {
        whenever(puestoParqueoRepository.findByIdWithPessimisticLock(1L)).thenReturn(Optional.of(puestoDisponible))

        assertThrows<SlotAlreadyAvailableException> {
            puestoParqueoService.liberarPuesto(1L, "jdoe", isGuard = false)
        }
    }

    @Test
    fun `liberarPuesto lanza HistorialParqueoNotFoundException cuando no hay historial activo`() {
        whenever(puestoParqueoRepository.findByIdWithPessimisticLock(2L)).thenReturn(Optional.of(puestoOcupado))
        whenever(historialParqueoRepository.findFirstByPuestoIdAndFechaSalidaIsNullOrderByFechaIngresoDesc(2L))
            .thenReturn(Optional.empty())

        assertThrows<HistorialParqueoNotFoundException> {
            puestoParqueoService.liberarPuesto(2L, "jdoe", isGuard = false)
        }
    }

    @Test
    fun `liberarPuesto lanza UnauthorizedAccessException cuando usuario no es el propietario`() {
        val historial = HistorialParqueo(id = 1L, puesto = puestoOcupado, username = "otro_usuario")
        whenever(puestoParqueoRepository.findByIdWithPessimisticLock(2L)).thenReturn(Optional.of(puestoOcupado))
        whenever(historialParqueoRepository.findFirstByPuestoIdAndFechaSalidaIsNullOrderByFechaIngresoDesc(2L))
            .thenReturn(Optional.of(historial))

        assertThrows<UnauthorizedAccessException> {
            puestoParqueoService.liberarPuesto(2L, "jdoe", isGuard = false)
        }
    }

    @Test
    fun `liberarPuesto permite al guard liberar un puesto de otro usuario`() {
        val historial = HistorialParqueo(id = 1L, puesto = puestoOcupado, username = "otro_usuario")
        whenever(puestoParqueoRepository.findByIdWithPessimisticLock(2L)).thenReturn(Optional.of(puestoOcupado))
        whenever(historialParqueoRepository.findFirstByPuestoIdAndFechaSalidaIsNullOrderByFechaIngresoDesc(2L))
            .thenReturn(Optional.of(historial))
        whenever(historialParqueoRepository.save(any())).thenReturn(historial)
        whenever(puestoParqueoRepository.save(any())).thenReturn(puestoOcupado)

        val result = puestoParqueoService.liberarPuesto(2L, "guardia1", isGuard = true)

        assertEquals(EstadoPuesto.DISPONIBLE, result.estado)
    }
}
