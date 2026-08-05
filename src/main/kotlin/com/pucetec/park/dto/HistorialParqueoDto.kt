package com.pucetec.park.dto

import java.time.LocalDateTime

data class HistorialParqueoResponse(
    val id: Long,
    val ticketCode: String,
    val username: String,
    val entryDate: LocalDateTime,
    val exitDate: LocalDateTime?,
    val vehiclePlate: String?,
    val space: PuestoParqueoResponse
)
