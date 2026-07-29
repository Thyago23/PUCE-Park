package com.pucetec.park.mappers

import com.pucetec.park.dto.CreatePuestoParqueoRequest
import com.pucetec.park.dto.PuestoParqueoResponse
import com.pucetec.park.entities.EstadoPuesto
import com.pucetec.park.entities.PuestoParqueo
import com.pucetec.park.entities.ZonaParqueo

fun CreatePuestoParqueoRequest.toEntity(zonaRef: ZonaParqueo) = PuestoParqueo(
    zona = zonaRef,
    numeroPuesto = this.numeroPuesto,
    estado = EstadoPuesto.DISPONIBLE
)

fun PuestoParqueo.toResponse() = PuestoParqueoResponse(
    id = this.id,
    numeroPuesto = this.numeroPuesto,
    estado = this.estado,
    zona = this.zona!!.toResponse()
)
