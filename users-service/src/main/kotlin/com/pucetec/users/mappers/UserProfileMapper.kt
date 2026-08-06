package com.pucetec.users.mappers

import com.pucetec.users.dto.UserProfileResponse
import com.pucetec.users.entities.UserProfile

fun UserProfile.toResponse() = UserProfileResponse(
    id = id,
    sub = sub,
    username = username,
    fullName = fullName,
    vehiclePlate = vehiclePlate,
    permitNumber = permitNumber,
    complete = fullName.isNotBlank() && (vehiclePlate.isNotBlank() || !permitNumber.isNullOrBlank()),
)
