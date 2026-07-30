package com.pucetec.park.dto

data class EstadisticasZonaResponse(
    val zonaId: Long,
    val zonaNombre: String,
    val capacidadMaxima: Int,
    val disponibles: Long,
    val ocupados: Long,
    val total: Long
)

data class EstadisticasPersonalesResponse(
    val mes: String,
    val totalSesiones: Long,
    val totalHoras: Double,
    val promedioHorasPorSesion: Double
)

data class RankingEntradaResponse(
    val posicion: Int,
    val username: String,
    val nombreCompleto: String,
    val totalHoras: Double,
    val totalSesiones: Long
)
