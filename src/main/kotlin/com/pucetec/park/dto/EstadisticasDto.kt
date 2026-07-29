package com.pucetec.park.dto

data class EstadisticasZonaResponse(
    val zonaId: Long,
    val zonaNombre: String,
    val capacidadMaxima: Int,
    val disponibles: Long,
    val ocupados: Long,
    val total: Long
)
