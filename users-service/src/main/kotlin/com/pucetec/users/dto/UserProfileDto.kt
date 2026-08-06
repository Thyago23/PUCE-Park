package com.pucetec.users.dto

data class UpdateUserProfileRequest(
    val fullName: String,
    val vehiclePlate: String,
    val permitNumber: String?,
)

data class UserProfileResponse(
    val id: Long,
    val sub: String,
    val username: String,
    val fullName: String,
    val vehiclePlate: String,
    val permitNumber: String?,
    val complete: Boolean,
)

data class ProfileStatusResponse(
    val complete: Boolean,
    val username: String,
)
