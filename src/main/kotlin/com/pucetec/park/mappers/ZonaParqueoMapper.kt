package com.pucetec.park.mappers

import com.pucetec.park.dto.CreateZonaParqueoRequest
import com.pucetec.park.dto.ZonaParqueoResponse
import com.pucetec.park.entities.ZonaParqueo

fun CreateZonaParqueoRequest.toEntity() = ZonaParqueo(
    nombre = this.nombre,
    descripcion = this.descripcion ?: "",
    capacidadMaxima = this.capacidadMaxima
)

fun ZonaParqueo.toResponse() = ZonaParqueoResponse(
    id = this.id,
    nombre = this.nombre,
    descripcion = this.descripcion,
    capacidadMaxima = this.capacidadMaxima
)
