package com.pucetec.park.mappers

import com.pucetec.park.dto.CreateZonaParqueoRequest
import com.pucetec.park.dto.ZonaParqueoResponse
import com.pucetec.park.entities.ZonaParqueo

fun CreateZonaParqueoRequest.toEntity() = ZonaParqueo(
    nombre = this.nombre,
    descripcion = this.descripcion ?: "",
    capacidadMaxima = this.capacidadMaxima
)

fun ZonaParqueo.toResponse(disponibles: Long = 0, ocupados: Long = 0) = ZonaParqueoResponse(
    id = this.id,
    nombre = this.nombre,
    descripcion = this.descripcion,
    capacidadMaxima = this.capacidadMaxima,
    puestosDisponibles = disponibles,
    puestosOcupados = ocupados,
    totalPuestos = disponibles + ocupados
)
