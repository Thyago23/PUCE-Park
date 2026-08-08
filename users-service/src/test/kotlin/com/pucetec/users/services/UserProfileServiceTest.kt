package com.pucetec.users.services

import com.pucetec.users.dto.UpdateUserProfileRequest
import com.pucetec.users.entities.UserProfile
import com.pucetec.users.exceptions.UserProfileNotFoundException
import com.pucetec.users.repositories.UserProfileRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class UserProfileServiceTest {

    @Mock private lateinit var userProfileRepository: UserProfileRepository
    @InjectMocks private lateinit var userProfileService: UserProfileService

    private fun perfil(
        sub: String = "sub-1", username: String = "jdoe",
        fullName: String = "Bryan Taco", plate: String = "PDY1233",
        permit: String? = "09334", dark: Boolean = false
    ) = UserProfile(id = 1L, sub = sub, username = username, fullName = fullName,
        vehiclePlate = plate, permitNumber = permit, darkMode = dark)

    @Test
    fun `getOrCreateProfile devuelve el perfil existente cuando ya existe`() {
        whenever(userProfileRepository.findBySub("sub-1")).thenReturn(Optional.of(perfil()))

        val res = userProfileService.getOrCreateProfile("sub-1", "jdoe")

        assertEquals("Bryan Taco", res.fullName)
        assertTrue(res.complete)
    }

    @Test
    fun `getOrCreateProfile crea un perfil vacío cuando no existe`() {
        whenever(userProfileRepository.findBySub("sub-x")).thenReturn(Optional.empty())
        whenever(userProfileRepository.save(any())).thenReturn(perfil(sub = "sub-x", fullName = "", plate = "", permit = null))

        val res = userProfileService.getOrCreateProfile("sub-x", "nuevo")

        assertEquals("sub-x", res.sub)
        assertFalse(res.complete)
    }

    @Test
    fun `getStatus devuelve complete true y sin faltantes cuando el perfil está completo`() {
        whenever(userProfileRepository.findBySub("sub-1")).thenReturn(Optional.of(perfil()))

        val res = userProfileService.getStatus("sub-1", "jdoe")

        assertTrue(res.complete)
        assertTrue(res.missing.isEmpty())
    }

    @Test
    fun `getStatus lista los campos faltantes cuando el perfil está incompleto`() {
        whenever(userProfileRepository.findBySub("sub-1"))
            .thenReturn(Optional.of(perfil(fullName = "", plate = "", permit = null)))

        val res = userProfileService.getStatus("sub-1", "jdoe")

        assertFalse(res.complete)
        assertTrue(res.missing.containsAll(listOf("fullName", "vehiclePlate", "permitNumber")))
    }

    @Test
    fun `getStatus devuelve incompleto cuando el perfil no existe`() {
        whenever(userProfileRepository.findBySub("sub-x")).thenReturn(Optional.empty())

        val res = userProfileService.getStatus("sub-x", "nuevo")

        assertFalse(res.complete)
        assertEquals(3, res.missing.size)
    }

    @Test
    fun `updateProfile actualiza y devuelve el perfil cuando existe`() {
        whenever(userProfileRepository.findBySub("sub-1")).thenReturn(Optional.of(perfil()))
        whenever(userProfileRepository.save(any())).thenAnswer { it.arguments[0] as UserProfile }

        val res = userProfileService.updateProfile(
            "sub-1", UpdateUserProfileRequest(fullName = "Ana Perez", vehiclePlate = "ABC1234", permitNumber = "12345", darkMode = true)
        )

        assertEquals("Ana Perez", res.fullName)
        assertEquals("ABC1234", res.vehiclePlate)
        assertTrue(res.darkMode)
    }

    @Test
    fun `updateProfile lanza UserProfileNotFoundException cuando el perfil no existe`() {
        whenever(userProfileRepository.findBySub("sub-x")).thenReturn(Optional.empty())

        assertThrows<UserProfileNotFoundException> {
            userProfileService.updateProfile("sub-x", UpdateUserProfileRequest("N", "P", "1", false))
        }
    }
}
