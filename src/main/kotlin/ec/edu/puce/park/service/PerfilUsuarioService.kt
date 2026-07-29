package ec.edu.puce.park.service

import ec.edu.puce.park.dto.PerfilUsuarioRequestDTO
import ec.edu.puce.park.dto.PerfilUsuarioResponseDTO
import ec.edu.puce.park.entity.PerfilUsuario
import ec.edu.puce.park.mapper.PerfilUsuarioMapper
import ec.edu.puce.park.repository.PerfilUsuarioRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface PerfilUsuarioService {
    fun getOrCreatePerfil(username: String): PerfilUsuarioResponseDTO
    fun updatePerfil(username: String, request: PerfilUsuarioRequestDTO): PerfilUsuarioResponseDTO
}

@Service
class PerfilUsuarioServiceImpl(
    private val repository: PerfilUsuarioRepository,
    private val mapper: PerfilUsuarioMapper
) : PerfilUsuarioService {

    @Transactional
    override fun getOrCreatePerfil(username: String): PerfilUsuarioResponseDTO {
        val perfil = repository.findByUsername(username).orElseGet {
            repository.save(PerfilUsuario(username = username))
        }
        return mapper.toDto(perfil)
    }

    @Transactional
    override fun updatePerfil(username: String, request: PerfilUsuarioRequestDTO): PerfilUsuarioResponseDTO {
        val perfil = repository.findByUsername(username).orElseGet {
            PerfilUsuario(username = username)
        }
        
        request.nombreCompleto?.let { perfil.nombreCompleto = it }
        request.placaVehiculo?.let { perfil.placaVehiculo = it }
        perfil.modoOscuro = request.modoOscuro
        
        return mapper.toDto(repository.save(perfil))
    }
}
