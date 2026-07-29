package com.pucetec.park.repositories

import com.pucetec.park.entities.HistorialParqueo
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface HistorialParqueoRepository : JpaRepository<HistorialParqueo, Long> {
    fun findFirstByPuestoIdAndFechaSalidaIsNullOrderByFechaIngresoDesc(puestoId: Long): Optional<HistorialParqueo>
}
