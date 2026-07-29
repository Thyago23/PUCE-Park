package ec.edu.puce.park.repository

import ec.edu.puce.park.entity.ZonaParqueo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ZonaParqueoRepository : JpaRepository<ZonaParqueo, Long>
