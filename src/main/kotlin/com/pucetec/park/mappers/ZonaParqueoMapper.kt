package com.pucetec.park.mappers

import com.pucetec.park.dto.CreateZonaParqueoRequest
import com.pucetec.park.dto.ZonaParqueoResponse
import com.pucetec.park.entities.ZonaParqueo

fun CreateZonaParqueoRequest.toEntity() = ZonaParqueo(
    nombre = this.name,
    descripcion = this.description ?: "",
    capacidadMaxima = this.maxCapacity,
    ubicacion = this.location ?: ""
)

fun ZonaParqueo.toResponse(disponibles: Long = 0, ocupados: Long = 0) = ZonaParqueoResponse(
    id = this.id,
    name = this.nombre,
    description = this.descripcion ?: "",
    location = this.ubicacion ?: "",
    maxCapacity = this.capacidadMaxima,
    availableSpaces = disponibles,
    occupiedSpaces = ocupados,
    totalSpaces = disponibles + ocupados
)
