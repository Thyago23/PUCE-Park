package ec.edu.puce.park.service

import ec.edu.puce.park.dto.ZonaParqueoRequestDTO
import ec.edu.puce.park.dto.ZonaParqueoResponseDTO
import ec.edu.puce.park.entity.ZonaParqueo
import ec.edu.puce.park.exception.ResourceNotFoundException
import ec.edu.puce.park.mapper.ZonaParqueoMapper
import ec.edu.puce.park.repository.ZonaParqueoRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface ZonaParqueoService {
    fun getAllZonas(): List<ZonaParqueoResponseDTO>
    fun createZona(request: ZonaParqueoRequestDTO): ZonaParqueoResponseDTO
    fun updateZona(id: Long, request: ZonaParqueoRequestDTO): ZonaParqueoResponseDTO
    fun deleteZona(id: Long)
}

@Service
class ZonaParqueoServiceImpl(
    private val repository: ZonaParqueoRepository,
    private val mapper: ZonaParqueoMapper
) : ZonaParqueoService {

    @Transactional(readOnly = true)
    override fun getAllZonas(): List<ZonaParqueoResponseDTO> {
        return repository.findAll().map { mapper.toDto(it) }
    }

    @Transactional
    override fun createZona(request: ZonaParqueoRequestDTO): ZonaParqueoResponseDTO {
        val zona = ZonaParqueo(
            nombre = request.nombre,
            descripcion = request.descripcion
        )
        return mapper.toDto(repository.save(zona))
    }

    @Transactional
    override fun updateZona(id: Long, request: ZonaParqueoRequestDTO): ZonaParqueoResponseDTO {
        val zona = repository.findById(id).orElseThrow { ResourceNotFoundException("Zona no encontrada") }
        zona.nombre = request.nombre
        zona.descripcion = request.descripcion
        return mapper.toDto(repository.save(zona))
    }

    @Transactional
    override fun deleteZona(id: Long) {
        val zona = repository.findById(id).orElseThrow { ResourceNotFoundException("Zona no encontrada") }
        repository.delete(zona)
    }
}
