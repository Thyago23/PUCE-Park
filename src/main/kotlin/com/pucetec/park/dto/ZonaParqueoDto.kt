package com.pucetec.park.dto

data class CreateZonaParqueoRequest(
    val name: String,
    val description: String? = null,
    val maxCapacity: Int,
    val location: String? = null
)

data class UpdateZonaParqueoRequest(
    val name: String,
    val description: String? = null,
    val maxCapacity: Int,
    val location: String? = null
)

data class ZonaParqueoResponse(
    val id: Long,
    val name: String,
    val description: String,
    val location: String,
    val maxCapacity: Int,
    val availableSpaces: Long,
    val occupiedSpaces: Long,
    val totalSpaces: Long
)
