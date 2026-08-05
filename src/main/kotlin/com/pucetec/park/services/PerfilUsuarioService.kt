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
        logger.info("Loading profile for user '$username'...")
        val existing = perfilUsuarioRepository.findByUsername(username)
        val perfil = if (existing.isPresent) {
            logger.info("Profile found for user '$username'. Returning existing profile.")
            existing.get()
        } else {
            logger.info("No profile found for '$username'. Creating new profile...")
            val created = perfilUsuarioRepository.save(PerfilUsuario(username = username))
            logger.info("New profile created for '$username' with id=${created.id}.")
            created
        }
        return perfil.toResponse()
    }

    @Transactional(readOnly = true)
    fun getEstadoPerfil(username: String): PerfilEstadoResponse {
        logger.info("Checking profile completeness for user '$username'...")
        val perfil = perfilUsuarioRepository.findByUsername(username).orElse(null)
        val missing = mutableListOf<String>()
        if (perfil == null || perfil.nombreCompleto.isBlank()) missing.add("fullName")
        if (perfil == null || perfil.placaVehiculo.isBlank()) missing.add("vehiclePlate")
        if (perfil == null || perfil.numeroPermiso.isNullOrBlank()) missing.add("permitNumber")
        val complete = missing.isEmpty()
        logger.info("Profile completeness for '$username': complete=$complete, missing=$missing.")
        return PerfilEstadoResponse(complete = complete, missing = missing)
    }

    @Transactional
    fun updatePerfil(username: String, request: UpdatePerfilUsuarioRequest): PerfilUsuarioResponse {
        logger.info("Updating profile for user '$username'...")
        val perfil = perfilUsuarioRepository.findByUsername(username).orElseGet {
            logger.info("No existing profile for '$username'. Creating new one before updating...")
            PerfilUsuario(username = username)
        }
        logger.info("Applying changes to profile '$username': fullName='${request.fullName}', plate='${request.vehiclePlate}'...")
        perfil.nombreCompleto = request.fullName
        perfil.placaVehiculo = request.vehiclePlate
        perfil.numeroPermiso = request.permitNumber
        perfil.modoOscuro = request.darkMode
        logger.info("Saving updated profile for '$username'...")
        val saved = perfilUsuarioRepository.save(perfil)
        logger.info("Profile for '$username' updated successfully (id=${saved.id}).")
        return saved.toResponse()
    }
}
