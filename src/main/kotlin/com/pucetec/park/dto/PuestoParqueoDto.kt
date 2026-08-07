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

// El cliente envía su nombre (que vive en users-service) para denormalizarlo en el
// historial y poder mostrarlo en el ranking sin joins entre servicios.
data class OcuparPuestoRequest(
    val fullName: String? = null
)

data class PuestoParqueoResponse(
    val id: Long,
    val spaceNumber: String,
    val row: String,
    val order: Int,
    val status: EstadoPuesto,
    val zone: ZonaParqueoResponse
)
