package com.pucetec.park.dto

data class CreatePuestoParqueoRequest(
    val zonaId: Long,
    val numeroPuesto: String
)

data class UpdatePuestoParqueoRequest(
    val zonaId: Long,
    val numeroPuesto: String
)

data class PuestoParqueoResponse(
    val id: Long,
    val numeroPuesto: String,
    val estado: String,
    val zona: ZonaParqueoResponse
)
