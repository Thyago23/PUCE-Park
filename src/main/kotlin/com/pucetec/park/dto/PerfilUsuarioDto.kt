package com.pucetec.park.dto

data class UpdatePerfilUsuarioRequest(
    val fullName: String,
    val vehiclePlate: String,
    val permitNumber: String,
    val darkMode: Boolean
)

data class PerfilUsuarioResponse(
    val id: Long,
    val username: String,
    val fullName: String,
    val vehiclePlate: String,
    val permitNumber: String,
    val darkMode: Boolean
)

data class PerfilEstadoResponse(
    val complete: Boolean,
    val missing: List<String>
)
