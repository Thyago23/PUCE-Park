package com.pucetec.park.repositories

import com.pucetec.park.entities.PerfilUsuario
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface PerfilUsuarioRepository : JpaRepository<PerfilUsuario, Long> {
    fun findByUsername(username: String): Optional<PerfilUsuario>
}
