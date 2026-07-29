package com.pucetec.park.dto

import com.pucetec.park.entities.EstadoPuesto

data class CreatePuestoParqueoRequest(
    val zonaId: Long,
    val numeroPuesto: String
)

data class UpdatePuestoParqueoRequest(
    val numeroPuesto: String
)

data class PuestoParqueoResponse(
    val id: Long,
    val numeroPuesto: String,
    val estado: EstadoPuesto,
    val zona: ZonaParqueoResponse
)
