package com.pucetec.park.repositories

import com.pucetec.park.entities.PuestoParqueo
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Optional

interface PuestoParqueoRepository : JpaRepository<PuestoParqueo, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM PuestoParqueo p WHERE p.id = :id")
    fun findByIdWithPessimisticLock(@Param("id") id: Long): Optional<PuestoParqueo>

    fun findByZonaId(zonaId: Long): List<PuestoParqueo>
}
