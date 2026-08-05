package com.pucetec.park.mappers

import com.pucetec.park.dto.PerfilUsuarioResponse
import com.pucetec.park.entities.PerfilUsuario

fun PerfilUsuario.toResponse() = PerfilUsuarioResponse(
    id = this.id,
    username = this.username,
    fullName = this.nombreCompleto,
    vehiclePlate = this.placaVehiculo,
    permitNumber = this.numeroPermiso ?: "",
    darkMode = this.modoOscuro
)
