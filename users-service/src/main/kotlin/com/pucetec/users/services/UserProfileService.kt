package com.pucetec.users.services

import com.pucetec.users.dto.ProfileStatusResponse
import com.pucetec.users.dto.UpdateUserProfileRequest
import com.pucetec.users.dto.UserProfileResponse
import com.pucetec.users.entities.UserProfile
import com.pucetec.users.exceptions.UserProfileNotFoundException
import com.pucetec.users.mappers.toResponse
import com.pucetec.users.repositories.UserProfileRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserProfileService(private val userProfileRepository: UserProfileRepository) {

    private val logger = LoggerFactory.getLogger(UserProfileService::class.java)

    @Transactional
    fun getOrCreateProfile(sub: String, username: String): UserProfileResponse {
        logger.info("Get or create profile for sub=$sub username=$username")
        val profile = userProfileRepository.findBySub(sub).orElseGet {
            logger.info("Creating new profile for sub=$sub username=$username")
            userProfileRepository.save(UserProfile(sub = sub, username = username))
        }
        return profile.toResponse()
    }

    @Transactional(readOnly = true)
    fun getStatus(sub: String, username: String): ProfileStatusResponse {
        logger.info("Get profile status for sub=$sub")
        val profile = userProfileRepository.findBySub(sub)
        return if (profile.isPresent) {
            val p = profile.get()
            val complete = p.fullName.isNotBlank() && (p.vehiclePlate.isNotBlank() || !p.permitNumber.isNullOrBlank())
            ProfileStatusResponse(complete = complete, username = p.username, missing = missingFields(p))
        } else {
            ProfileStatusResponse(complete = false, username = username, missing = listOf("fullName", "vehiclePlate", "permitNumber"))
        }
    }

    @Transactional
    fun updateProfile(sub: String, request: UpdateUserProfileRequest): UserProfileResponse {
        logger.info("Updating profile for sub=$sub")
        val profile = userProfileRepository.findBySub(sub)
            .orElseThrow { UserProfileNotFoundException("Profile for sub $sub not found") }
        profile.fullName = request.fullName
        profile.vehiclePlate = request.vehiclePlate
        profile.permitNumber = request.permitNumber
        profile.darkMode = request.darkMode
        return userProfileRepository.save(profile).toResponse()
    }

    private fun missingFields(p: UserProfile): List<String> = buildList {
        if (p.fullName.isBlank()) add("fullName")
        if (p.vehiclePlate.isBlank()) add("vehiclePlate")
        if (p.permitNumber.isNullOrBlank()) add("permitNumber")
    }
}
