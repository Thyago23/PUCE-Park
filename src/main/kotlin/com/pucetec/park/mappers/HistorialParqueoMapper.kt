package com.pucetec.park.mappers

import com.pucetec.park.dto.HistorialParqueoResponse
import com.pucetec.park.entities.HistorialParqueo

fun HistorialParqueo.toResponse() = HistorialParqueoResponse(
    id = this.id,
    codigoTicket = this.codigoTicket,
    username = this.username,
    fechaIngreso = this.fechaIngreso,
    fechaSalida = this.fechaSalida,
    puesto = this.puesto!!.toResponse()
)
