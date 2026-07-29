package ec.edu.puce.park.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { it.configurationSource(corsConfigurationSource()) }
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                // Públicos
                auth.requestMatchers(HttpMethod.GET, "/api/v1/zonas").permitAll()
                auth.requestMatchers(HttpMethod.GET, "/api/v1/puestos/zona/**").permitAll()

                // DRIVER
                auth.requestMatchers(HttpMethod.POST, "/api/v1/puestos/*/ocupar").hasRole("DRIVER")
                auth.requestMatchers(HttpMethod.POST, "/api/v1/puestos/*/liberar").hasRole("DRIVER")
                auth.requestMatchers(HttpMethod.GET, "/api/v1/perfil/me").hasRole("DRIVER")
                auth.requestMatchers(HttpMethod.PUT, "/api/v1/perfil/me").hasRole("DRIVER")

                // GUARD
                auth.requestMatchers(HttpMethod.PATCH, "/api/v1/puestos/*/forzar-liberacion").hasRole("GUARD")

                // ADMIN
                auth.requestMatchers(HttpMethod.POST, "/api/v1/zonas").hasRole("ADMIN")
                auth.requestMatchers(HttpMethod.PUT, "/api/v1/zonas/*").hasRole("ADMIN")
                auth.requestMatchers(HttpMethod.DELETE, "/api/v1/zonas/*").hasRole("ADMIN")
                auth.requestMatchers(HttpMethod.POST, "/api/v1/puestos").hasRole("ADMIN")
                auth.requestMatchers(HttpMethod.PUT, "/api/v1/puestos/*").hasRole("ADMIN")
                auth.requestMatchers(HttpMethod.DELETE, "/api/v1/puestos/*").hasRole("ADMIN")

                auth.anyRequest().authenticated()
            }
            .oauth2ResourceServer { oauth2 ->
                oauth2.jwt { jwt ->
                    jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())
                }
            }

        return http.build()
    }

    @Bean
    fun jwtAuthenticationConverter(): Converter<Jwt, AbstractAuthenticationToken> {
        val jwtAuthenticationConverter = JwtAuthenticationConverter()
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter { jwt ->
            val groups = jwt.getClaimAsStringList("cognito:groups") ?: emptyList()
            groups.map { SimpleGrantedAuthority("ROLE_${it.uppercase()}") }
        }
        return jwtAuthenticationConverter
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration().apply {
            allowedOrigins = listOf("*")
            allowedMethods = listOf("*")
            allowedHeaders = listOf("*")
        }
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}
