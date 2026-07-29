package ec.edu.puce.park.service

import ec.edu.puce.park.dto.ZonaParqueoRequestDTO
import ec.edu.puce.park.entity.ZonaParqueo
import ec.edu.puce.park.exception.ResourceNotFoundException
import ec.edu.puce.park.mapper.ZonaParqueoMapper
import ec.edu.puce.park.repository.ZonaParqueoRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.Optional

class ZonaParqueoServiceTest {

    private val repository: ZonaParqueoRepository = mockk()
    private val mapper: ZonaParqueoMapper = ZonaParqueoMapper()
    private val service = ZonaParqueoServiceImpl(repository, mapper)

    @Test
    fun `should create zona successfully`() {
        val request = ZonaParqueoRequestDTO(nombre = "Zona A", descripcion = "Desc A")
        val savedEntity = ZonaParqueo(id = 1L, nombre = "Zona A", descripcion = "Desc A")

        every { repository.save(any()) } returns savedEntity

        val response = service.createZona(request)

        assertNotNull(response)
        assertEquals(1L, response.id)
        assertEquals("Zona A", response.nombre)
        verify(exactly = 1) { repository.save(any()) }
    }

    @Test
    fun `should get all zonas`() {
        val entities = listOf(ZonaParqueo(id = 1L, nombre = "Zona A"))
        every { repository.findAll() } returns entities

        val responses = service.getAllZonas()

        assertEquals(1, responses.size)
        assertEquals("Zona A", responses[0].nombre)
    }

    @Test
    fun `should throw exception when deleting non-existent zona`() {
        every { repository.findById(99L) } returns Optional.empty()

        assertThrows<ResourceNotFoundException> {
            service.deleteZona(99L)
        }
    }
}
