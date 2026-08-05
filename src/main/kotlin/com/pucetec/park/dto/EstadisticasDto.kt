package com.pucetec.park.dto

data class EstadisticasZonaResponse(
    val zoneId: Long,
    val zoneName: String,
    val maxCapacity: Int,
    val available: Long,
    val occupied: Long,
    val total: Long
)

data class EstadisticasPersonalesResponse(
    val month: String,
    val totalSessions: Long,
    val totalHours: Double,
    val avgHoursPerSession: Double
)

data class RankingEntradaResponse(
    val position: Int,
    val username: String,
    val fullName: String,
    val totalHours: Double,
    val totalSessions: Long
)
