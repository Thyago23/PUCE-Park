package com.pucetec.park.dto

data class CreateZonaParqueoRequest(
    val nombre: String,
    val descripcion: String = ""
)

data class UpdateZonaParqueoRequest(
    val nombre: String,
    val descripcion: String = ""
)

data class ZonaParqueoResponse(
    val id: Long,
    val nombre: String,
    val descripcion: String
)
