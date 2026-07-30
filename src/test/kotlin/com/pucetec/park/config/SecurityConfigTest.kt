package com.pucetec.park.config

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt

class SecurityConfigTest {

    private val converter = SecurityConfig().cognitoGroupsConverter()

    private fun jwtWithClaims(builder: Jwt.Builder.() -> Unit): Jwt =
        Jwt.withTokenValue("fake-token")
            .header("alg", "none")
            .subject("test-user")
            .apply(builder)
            .build()

    @Test
    fun `el grupo DRIVER se convierte en la authority ROLE_DRIVER`() {
        val jwt = jwtWithClaims { claim("cognito:groups", listOf("DRIVER")) }
        val authentication = converter.convert(jwt)
        assertTrue(authentication!!.authorities.contains(SimpleGrantedAuthority("ROLE_DRIVER")))
    }

    @Test
    fun `el grupo GUARD se convierte en la authority ROLE_GUARD`() {
        val jwt = jwtWithClaims { claim("cognito:groups", listOf("GUARD")) }
        val authentication = converter.convert(jwt)
        assertTrue(authentication!!.authorities.contains(SimpleGrantedAuthority("ROLE_GUARD")))
    }

    @Test
    fun `el grupo ADMIN se convierte en la authority ROLE_ADMIN`() {
        val jwt = jwtWithClaims { claim("cognito:groups", listOf("ADMIN")) }
        val authentication = converter.convert(jwt)
        assertTrue(authentication!!.authorities.contains(SimpleGrantedAuthority("ROLE_ADMIN")))
    }

    @Test
    fun `grupos en minusculas se normalizan a mayusculas`() {
        val jwt = jwtWithClaims { claim("cognito:groups", listOf("driver")) }
        val authentication = converter.convert(jwt)
        assertTrue(authentication!!.authorities.contains(SimpleGrantedAuthority("ROLE_DRIVER")))
    }

    @Test
    fun `varios grupos generan una authority por cada uno`() {
        val jwt = jwtWithClaims { claim("cognito:groups", listOf("DRIVER", "ADMIN")) }
        val authentication = converter.convert(jwt)
        assertTrue(authentication!!.authorities.contains(SimpleGrantedAuthority("ROLE_DRIVER")))
        assertTrue(authentication.authorities.contains(SimpleGrantedAuthority("ROLE_ADMIN")))
    }

    @Test
    fun `un JWT sin el claim cognito-groups no genera roles`() {
        val jwt = jwtWithClaims { }
        val authentication = converter.convert(jwt)
        assertEquals(0, authentication!!.authorities.count { it.authority?.startsWith("ROLE_") == true })
    }
}
