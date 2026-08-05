package com.pucetec.park.mappers

import com.pucetec.park.dto.CreatePuestoParqueoRequest
import com.pucetec.park.dto.PuestoParqueoResponse
import com.pucetec.park.entities.EstadoPuesto
import com.pucetec.park.entities.PuestoParqueo
import com.pucetec.park.entities.ZonaParqueo

fun CreatePuestoParqueoRequest.toEntity(zonaRef: ZonaParqueo) = PuestoParqueo(
    zona = zonaRef,
    numeroPuesto = this.spaceNumber,
    fila = this.row,
    orden = this.order,
    estado = EstadoPuesto.DISPONIBLE
)

fun PuestoParqueo.toResponse() = PuestoParqueoResponse(
    id = this.id,
    spaceNumber = this.numeroPuesto,
    row = this.fila,
    order = this.orden,
    status = this.estado,
    zone = this.zona!!.toResponse()
)
