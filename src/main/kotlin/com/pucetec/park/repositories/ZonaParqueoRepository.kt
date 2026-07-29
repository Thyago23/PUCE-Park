package com.pucetec.park.repositories

import com.pucetec.park.entities.ZonaParqueo
import org.springframework.data.jpa.repository.JpaRepository

interface ZonaParqueoRepository : JpaRepository<ZonaParqueo, Long>
