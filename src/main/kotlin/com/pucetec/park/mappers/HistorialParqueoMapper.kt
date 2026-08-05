package com.pucetec.park.mappers

import com.pucetec.park.dto.HistorialParqueoResponse
import com.pucetec.park.entities.HistorialParqueo

fun HistorialParqueo.toResponse() = HistorialParqueoResponse(
    id = this.id,
    ticketCode = this.codigoTicket,
    username = this.username,
    entryDate = this.fechaIngreso,
    exitDate = this.fechaSalida,
    vehiclePlate = this.placaVehiculo,
    space = this.puesto!!.toResponse()
)
