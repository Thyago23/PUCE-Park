package ec.edu.puce.park.repository

import ec.edu.puce.park.entity.PerfilUsuario
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface PerfilUsuarioRepository : JpaRepository<PerfilUsuario, Long> {
    fun findByUsername(username: String): Optional<PerfilUsuario>
}
