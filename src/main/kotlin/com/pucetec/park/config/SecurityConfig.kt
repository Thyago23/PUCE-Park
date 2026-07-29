package com.pucetec.park.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth.requestMatchers(HttpMethod.GET, "/api/v1/zonas").permitAll()
                auth.requestMatchers(HttpMethod.GET, "/api/v1/puestos/zona/**").permitAll()
                
                auth.requestMatchers(HttpMethod.PUT, "/api/v1/puestos/*/ocupar").hasRole("DRIVER")
                auth.requestMatchers(HttpMethod.PUT, "/api/v1/puestos/*/liberar").hasRole("DRIVER")
                auth.requestMatchers("/api/v1/perfil/me").hasRole("DRIVER")

                auth.requestMatchers(HttpMethod.PUT, "/api/v1/puestos/*/forzar-liberacion").hasRole("GUARD")

                auth.requestMatchers("/api/v1/zonas/**").hasRole("ADMIN")
                auth.requestMatchers(HttpMethod.POST, "/api/v1/puestos").hasRole("ADMIN")
                
                auth.anyRequest().authenticated()
            }
            .oauth2ResourceServer { oauth2 ->
                oauth2.jwt { jwt -> jwt.jwtAuthenticationConverter(cognitoGroupsConverter()) }
            }
        return http.build()
    }

    @Bean
    fun cognitoGroupsConverter(): JwtAuthenticationConverter {
        val converter = JwtAuthenticationConverter()
        converter.setJwtGrantedAuthoritiesConverter { jwt ->
            val groups = jwt.getClaimAsStringList("cognito:groups") ?: emptyList()
            groups.map { SimpleGrantedAuthority("ROLE_${it.uppercase()}") }
        }
        return converter
    }
}
