package ec.edu.puce.park.service

import ec.edu.puce.park.dto.PuestoParqueoRequestDTO
import ec.edu.puce.park.dto.PuestoParqueoResponseDTO
import ec.edu.puce.park.entity.EstadoPuesto
import ec.edu.puce.park.entity.HistorialParqueo
import ec.edu.puce.park.entity.PuestoParqueo
import ec.edu.puce.park.exception.ResourceNotFoundException
import ec.edu.puce.park.exception.SlotAlreadyOccupiedException
import ec.edu.puce.park.exception.UnauthorizedAccessException
import ec.edu.puce.park.mapper.PuestoParqueoMapper
import ec.edu.puce.park.repository.HistorialParqueoRepository
import ec.edu.puce.park.repository.PuestoParqueoRepository
import ec.edu.puce.park.repository.ZonaParqueoRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

interface PuestoParqueoService {
    fun getPuestosByZona(zonaId: Long): List<PuestoParqueoResponseDTO>
    fun createPuesto(request: PuestoParqueoRequestDTO): PuestoParqueoResponseDTO
    fun updatePuesto(id: Long, request: PuestoParqueoRequestDTO): PuestoParqueoResponseDTO
    fun deletePuesto(id: Long)
    fun ocuparPuesto(id: Long, username: String): PuestoParqueoResponseDTO
    fun liberarPuesto(id: Long, username: String, isGuard: Boolean): PuestoParqueoResponseDTO
}

@Service
class PuestoParqueoServiceImpl(
    private val repository: PuestoParqueoRepository,
    private val zonaRepository: ZonaParqueoRepository,
    private val historialRepository: HistorialParqueoRepository,
    private val mapper: PuestoParqueoMapper
) : PuestoParqueoService {

    @Transactional(readOnly = true)
    override fun getPuestosByZona(zonaId: Long): List<PuestoParqueoResponseDTO> {
        return repository.findByZonaId(zonaId).map { mapper.toDto(it) }
    }

    @Transactional
    override fun createPuesto(request: PuestoParqueoRequestDTO): PuestoParqueoResponseDTO {
        val zona = zonaRepository.findById(request.zonaId)
            .orElseThrow { ResourceNotFoundException("Zona no encontrada") }
        val puesto = PuestoParqueo(
            zona = zona,
            numeroPuesto = request.numeroPuesto
        )
        return mapper.toDto(repository.save(puesto))
    }

    @Transactional
    override fun updatePuesto(id: Long, request: PuestoParqueoRequestDTO): PuestoParqueoResponseDTO {
        val puesto = repository.findById(id).orElseThrow { ResourceNotFoundException("Puesto no encontrado") }
        val zona = zonaRepository.findById(request.zonaId)
            .orElseThrow { ResourceNotFoundException("Zona no encontrada") }
        
        puesto.numeroPuesto = request.numeroPuesto
        puesto.zona = zona
        return mapper.toDto(repository.save(puesto))
    }

    @Transactional
    override fun deletePuesto(id: Long) {
        val puesto = repository.findById(id).orElseThrow { ResourceNotFoundException("Puesto no encontrado") }
        repository.delete(puesto)
    }

    @Transactional
    override fun ocuparPuesto(id: Long, username: String): PuestoParqueoResponseDTO {
        val puesto = repository.findByIdWithPessimisticLock(id)
            .orElseThrow { ResourceNotFoundException("Puesto no encontrado") }

        if (puesto.estado == EstadoPuesto.OCUPADO) {
            throw SlotAlreadyOccupiedException("El puesto ya está ocupado")
        }

        puesto.estado = EstadoPuesto.OCUPADO
        val historial = HistorialParqueo(
            puesto = puesto,
            username = username
        )
        historialRepository.save(historial)
        return mapper.toDto(repository.save(puesto))
    }

    @Transactional
    override fun liberarPuesto(id: Long, username: String, isGuard: Boolean): PuestoParqueoResponseDTO {
        val puesto = repository.findByIdWithPessimisticLock(id)
            .orElseThrow { ResourceNotFoundException("Puesto no encontrado") }

        if (puesto.estado == EstadoPuesto.DISPONIBLE) {
            throw IllegalStateException("El puesto ya está disponible")
        }

        val historial = historialRepository.findFirstByPuestoIdAndFechaSalidaIsNullOrderByFechaIngresoDesc(puesto.id)
            .orElseThrow { ResourceNotFoundException("Registro de historial no encontrado para este puesto") }

        if (!isGuard && historial.username != username) {
            throw UnauthorizedAccessException("No tienes permiso para liberar este puesto")
        }

        historial.fechaSalida = LocalDateTime.now()
        historialRepository.save(historial)

        puesto.estado = EstadoPuesto.DISPONIBLE
        return mapper.toDto(repository.save(puesto))
    }
}
