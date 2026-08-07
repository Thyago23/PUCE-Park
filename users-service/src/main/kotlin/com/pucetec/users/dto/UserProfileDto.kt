package com.pucetec.users.dto

data class UpdateUserProfileRequest(
    val fullName: String,
    val vehiclePlate: String,
    val permitNumber: String?,
    val darkMode: Boolean = false,
)

data class UserProfileResponse(
    val id: Long,
    val sub: String,
    val username: String,
    val fullName: String,
    val vehiclePlate: String,
    val permitNumber: String,
    val darkMode: Boolean,
    val complete: Boolean,
)

data class ProfileStatusResponse(
    val complete: Boolean,
    val username: String,
    val missing: List<String>,
)
