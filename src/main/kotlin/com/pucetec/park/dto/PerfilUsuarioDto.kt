package com.pucetec.park.dto

data class UpdatePerfilUsuarioRequest(
    val nombreCompleto: String,
    val placaVehiculo: String,
    val modoOscuro: Boolean
)

data class PerfilUsuarioResponse(
    val id: Long,
    val username: String,
    val nombreCompleto: String,
    val placaVehiculo: String,
    val modoOscuro: Boolean
)
