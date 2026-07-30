package com.pucetec.park.mappers

import com.pucetec.park.dto.PerfilUsuarioResponse
import com.pucetec.park.entities.PerfilUsuario

fun PerfilUsuario.toResponse() = PerfilUsuarioResponse(
    id = this.id,
    username = this.username,
    nombreCompleto = this.nombreCompleto,
    placaVehiculo = this.placaVehiculo,
    numeroPermiso = this.numeroPermiso,
    modoOscuro = this.modoOscuro
)
