package ec.edu.puce.park.repository

import ec.edu.puce.park.entity.HistorialParqueo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface HistorialParqueoRepository : JpaRepository<HistorialParqueo, Long> {
    fun findFirstByPuestoIdAndFechaSalidaIsNullOrderByFechaIngresoDesc(puestoId: Long): Optional<HistorialParqueo>
}
