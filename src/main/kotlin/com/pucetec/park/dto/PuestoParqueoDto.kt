package com.pucetec.park.dto

import com.pucetec.park.entities.EstadoPuesto

data class CreatePuestoParqueoRequest(
    val zoneId: Long,
    val spaceNumber: String,
    val row: String,
    val order: Int
)

data class UpdatePuestoParqueoRequest(
    val spaceNumber: String
)

data class ForzarOcupacionRequest(
    val vehiclePlate: String
)

data class PuestoParqueoResponse(
    val id: Long,
    val spaceNumber: String,
    val row: String,
    val order: Int,
    val status: EstadoPuesto,
    val zone: ZonaParqueoResponse
)
