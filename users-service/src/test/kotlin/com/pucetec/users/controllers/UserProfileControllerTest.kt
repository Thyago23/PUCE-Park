package com.pucetec.users.controllers

import com.pucetec.users.config.SecurityConfig
import com.pucetec.users.dto.ProfileStatusResponse
import com.pucetec.users.dto.UserProfileResponse
import com.pucetec.users.exceptions.GlobalExceptionHandler
import com.pucetec.users.services.UserProfileService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(UserProfileController::class)
@Import(SecurityConfig::class, GlobalExceptionHandler::class)
class UserProfileControllerTest {

    @Autowired private lateinit var mockMvc: MockMvc
    @MockitoBean private lateinit var userProfileService: UserProfileService
    @MockitoBean private lateinit var jwtDecoder: JwtDecoder

    private val profile = UserProfileResponse(
        id = 1L, sub = "sub-1", username = "jdoe", fullName = "Bryan Taco",
        vehiclePlate = "PDY1233", permitNumber = "09334", darkMode = false, complete = true
    )

    @Test
    fun `GET users me sin token responde 401`() {
        mockMvc.perform(get("/users/me")).andExpect(status().isUnauthorized)
    }

    @Test
    fun `GET users me con token responde 200`() {
        whenever(userProfileService.getOrCreateProfile(any(), any())).thenReturn(profile)

        mockMvc.perform(get("/users/me").with(jwt()))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.fullName").value("Bryan Taco"))
            .andExpect(jsonPath("$.complete").value(true))
    }

    @Test
    fun `GET users me estado con token responde 200`() {
        whenever(userProfileService.getStatus(any(), any()))
            .thenReturn(ProfileStatusResponse(complete = true, username = "jdoe", missing = emptyList()))

        mockMvc.perform(get("/users/me/estado").with(jwt()))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.complete").value(true))
    }

    @Test
    fun `PUT users me sin token responde 401`() {
        mockMvc.perform(
            put("/users/me").contentType(MediaType.APPLICATION_JSON)
                .content("""{"fullName":"X","vehiclePlate":"Y","permitNumber":"1","darkMode":false}""")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `PUT users me con token responde 200`() {
        whenever(userProfileService.updateProfile(any(), any())).thenReturn(profile)

        mockMvc.perform(
            put("/users/me").with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"fullName":"Bryan Taco","vehiclePlate":"PDY1233","permitNumber":"09334","darkMode":true}""")
        ).andExpect(status().isOk)
    }
}
