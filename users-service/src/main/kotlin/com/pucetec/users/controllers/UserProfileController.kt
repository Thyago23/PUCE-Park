package com.pucetec.users.controllers

import com.pucetec.users.dto.ProfileStatusResponse
import com.pucetec.users.dto.UpdateUserProfileRequest
import com.pucetec.users.dto.UserProfileResponse
import com.pucetec.users.services.UserProfileService
import org.slf4j.LoggerFactory
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users/me")
class UserProfileController(private val userProfileService: UserProfileService) {

    private val logger = LoggerFactory.getLogger(UserProfileController::class.java)

    @GetMapping
    fun getMyProfile(@AuthenticationPrincipal jwt: Jwt): UserProfileResponse {
        val sub = jwt.subject
        val username = jwt.getClaimAsString("cognito:username") ?: jwt.getClaimAsString("username") ?: sub
        logger.info("GET /users/me - sub=$sub username=$username")
        return userProfileService.getOrCreateProfile(sub, username)
    }

    @GetMapping("/estado")
    fun getMyStatus(@AuthenticationPrincipal jwt: Jwt): ProfileStatusResponse {
        val sub = jwt.subject
        val username = jwt.getClaimAsString("cognito:username") ?: jwt.getClaimAsString("username") ?: sub
        logger.info("GET /users/me/estado - sub=$sub")
        return userProfileService.getStatus(sub, username)
    }

    @PutMapping
    fun updateMyProfile(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody request: UpdateUserProfileRequest
    ): UserProfileResponse {
        val sub = jwt.subject
        logger.info("PUT /users/me - sub=$sub")
        return userProfileService.updateProfile(sub, request)
    }
}
