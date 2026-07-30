package com.pucetec.park.services

import com.pucetec.park.dto.PerfilEstadoResponse
import com.pucetec.park.dto.UpdatePerfilUsuarioRequest
import com.pucetec.park.dto.PerfilUsuarioResponse
import com.pucetec.park.entities.PerfilUsuario
import com.pucetec.park.mappers.toResponse
import com.pucetec.park.repositories.PerfilUsuarioRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PerfilUsuarioService(
    private val perfilUsuarioRepository: PerfilUsuarioRepository
) {
    private val logger = LoggerFactory.getLogger(PerfilUsuarioService::class.java)

    @Transactional
    fun getOrCreatePerfil(username: String): PerfilUsuarioResponse {
        logger.info("Getting or creating profile for $username")
        val perfil = perfilUsuarioRepository.findByUsername(username).orElseGet {
            perfilUsuarioRepository.save(PerfilUsuario(username = username))
        }
        return perfil.toResponse()
    }

    @Transactional(readOnly = true)
    fun getEstadoPerfil(username: String): PerfilEstadoResponse {
        logger.info("Checking profile completeness for $username")
        val perfil = perfilUsuarioRepository.findByUsername(username).orElse(null)
        val faltante = mutableListOf<String>()
        if (perfil == null || perfil.nombreCompleto.isBlank()) faltante.add("nombreCompleto")
        if (perfil == null || perfil.placaVehiculo.isBlank()) faltante.add("placaVehiculo")
        if (perfil == null || perfil.numeroPermiso.isBlank()) faltante.add("numeroPermiso")
        return PerfilEstadoResponse(completo = faltante.isEmpty(), faltante = faltante)
    }

    @Transactional
    fun updatePerfil(username: String, request: UpdatePerfilUsuarioRequest): PerfilUsuarioResponse {
        logger.info("Updating profile for $username")
        val perfil = perfilUsuarioRepository.findByUsername(username).orElseGet {
            PerfilUsuario(username = username)
        }
        perfil.nombreCompleto = request.nombreCompleto
        perfil.placaVehiculo = request.placaVehiculo
        perfil.numeroPermiso = request.numeroPermiso
        perfil.modoOscuro = request.modoOscuro
        return perfilUsuarioRepository.save(perfil).toResponse()
    }
}
