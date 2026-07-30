package com.pucetec.park.dto

data class CreateZonaParqueoRequest(
    val nombre: String,
    val descripcion: String? = null,
    val capacidadMaxima: Int,
    val ubicacion: String? = null
)

data class UpdateZonaParqueoRequest(
    val nombre: String,
    val descripcion: String? = null,
    val capacidadMaxima: Int,
    val ubicacion: String? = null
)

data class ZonaParqueoResponse(
    val id: Long,
    val nombre: String,
    val descripcion: String,
    val ubicacion: String,
    val capacidadMaxima: Int,
    val puestosDisponibles: Long,
    val puestosOcupados: Long,
    val totalPuestos: Long
)
