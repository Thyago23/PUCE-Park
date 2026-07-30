package com.pucetec.park.dto

import java.time.LocalDateTime

data class HistorialParqueoResponse(
    val id: Long,
    val codigoTicket: String,
    val username: String,
    val fechaIngreso: LocalDateTime,
    val fechaSalida: LocalDateTime?,
    val puesto: PuestoParqueoResponse
)
