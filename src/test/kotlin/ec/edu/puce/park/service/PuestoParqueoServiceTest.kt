package ec.edu.puce.park.service

import ec.edu.puce.park.entity.EstadoPuesto
import ec.edu.puce.park.entity.HistorialParqueo
import ec.edu.puce.park.entity.PuestoParqueo
import ec.edu.puce.park.entity.ZonaParqueo
import ec.edu.puce.park.exception.ResourceNotFoundException
import ec.edu.puce.park.exception.SlotAlreadyOccupiedException
import ec.edu.puce.park.exception.UnauthorizedAccessException
import ec.edu.puce.park.mapper.PuestoParqueoMapper
import ec.edu.puce.park.repository.HistorialParqueoRepository
import ec.edu.puce.park.repository.PuestoParqueoRepository
import ec.edu.puce.park.repository.ZonaParqueoRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.Optional

class PuestoParqueoServiceTest {

    private val puestoRepository: PuestoParqueoRepository = mockk()
    private val zonaRepository: ZonaParqueoRepository = mockk()
    private val historialRepository: HistorialParqueoRepository = mockk()
    private val mapper: PuestoParqueoMapper = PuestoParqueoMapper()

    private val service = PuestoParqueoServiceImpl(
        puestoRepository, zonaRepository, historialRepository, mapper
    )

    @Test
    fun `should occupy slot successfully`() {
        val zona = ZonaParqueo(id = 1L, nombre = "Zona A")
        val puesto = PuestoParqueo(id = 1L, zona = zona, numeroPuesto = "A1", estado = EstadoPuesto.DISPONIBLE)
        
        every { puestoRepository.findByIdWithPessimisticLock(1L) } returns Optional.of(puesto)
        every { historialRepository.save(any()) } answers { firstArg() }
        every { puestoRepository.save(any()) } answers { firstArg() }

        val response = service.ocuparPuesto(1L, "driver1")

        assertEquals(EstadoPuesto.OCUPADO.name, response.estado)
        verify(exactly = 1) { historialRepository.save(any()) }
    }

    @Test
    fun `should throw error when trying to occupy already occupied slot`() {
        val zona = ZonaParqueo(id = 1L, nombre = "Zona A")
        val puesto = PuestoParqueo(id = 1L, zona = zona, numeroPuesto = "A1", estado = EstadoPuesto.OCUPADO)
        
        every { puestoRepository.findByIdWithPessimisticLock(1L) } returns Optional.of(puesto)

        assertThrows<SlotAlreadyOccupiedException> {
            service.ocuparPuesto(1L, "driver1")
        }
    }

    @Test
    fun `should free slot successfully if user is owner`() {
        val zona = ZonaParqueo(id = 1L, nombre = "Zona A")
        val puesto = PuestoParqueo(id = 1L, zona = zona, numeroPuesto = "A1", estado = EstadoPuesto.OCUPADO)
        val historial = HistorialParqueo(id = 1L, puesto = puesto, username = "driver1")

        every { puestoRepository.findByIdWithPessimisticLock(1L) } returns Optional.of(puesto)
        every { historialRepository.findFirstByPuestoIdAndFechaSalidaIsNullOrderByFechaIngresoDesc(1L) } returns Optional.of(historial)
        every { historialRepository.save(any()) } answers { firstArg() }
        every { puestoRepository.save(any()) } answers { firstArg() }

        val response = service.liberarPuesto(1L, "driver1", isGuard = false)

        assertEquals(EstadoPuesto.DISPONIBLE.name, response.estado)
        assertNotNull(historial.fechaSalida)
    }

    @Test
    fun `should throw error when trying to free slot and user is not owner and not guard`() {
        val zona = ZonaParqueo(id = 1L, nombre = "Zona A")
        val puesto = PuestoParqueo(id = 1L, zona = zona, numeroPuesto = "A1", estado = EstadoPuesto.OCUPADO)
        val historial = HistorialParqueo(id = 1L, puesto = puesto, username = "driver1")

        every { puestoRepository.findByIdWithPessimisticLock(1L) } returns Optional.of(puesto)
        every { historialRepository.findFirstByPuestoIdAndFechaSalidaIsNullOrderByFechaIngresoDesc(1L) } returns Optional.of(historial)

        assertThrows<UnauthorizedAccessException> {
            service.liberarPuesto(1L, "driver2", isGuard = false)
        }
    }

    @Test
    fun `should free slot successfully if user is guard even if not owner`() {
        val zona = ZonaParqueo(id = 1L, nombre = "Zona A")
        val puesto = PuestoParqueo(id = 1L, zona = zona, numeroPuesto = "A1", estado = EstadoPuesto.OCUPADO)
        val historial = HistorialParqueo(id = 1L, puesto = puesto, username = "driver1")

        every { puestoRepository.findByIdWithPessimisticLock(1L) } returns Optional.of(puesto)
        every { historialRepository.findFirstByPuestoIdAndFechaSalidaIsNullOrderByFechaIngresoDesc(1L) } returns Optional.of(historial)
        every { historialRepository.save(any()) } answers { firstArg() }
        every { puestoRepository.save(any()) } answers { firstArg() }

        val response = service.liberarPuesto(1L, "guard1", isGuard = true)

        assertEquals(EstadoPuesto.DISPONIBLE.name, response.estado)
    }
}
