package com.una.tango

import jakarta.annotation.Resource
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

/**
 *  Configuración para initlocal.
 */
@Profile("initlocal")
@Configuration
@EnableWebSecurity
class OpenSecurityConfiguration{
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain{
        // No CSRF and CORS protection y permitir cualquier request autenticado.
        http
            .csrf{
                it.disable()
            }.cors{
                it.disable()
            }.authorizeHttpRequests{
                it.anyRequest().authenticated()
            }
        return http.build()
    }

    /**
     * Necesario agregar este Bean para encriptar contraseñas al correr las pruebas.
     * */
    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder?{
        return BCryptPasswordEncoder()
    }
}

/**
 * Configuración para cualquier otro perfil.
 * Login y signup tienen que estar desprotegidos.
 */
@Profile("!initlocal") // Every profile but 'initlocal'.
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class JwtSecurityConfiguration{

    @Value("\${url.unsecure}")
    val URL_UNSECURE: String? = null

    @Value("\${url.user.signup}")
    val URL_SIGNUP: String? = null
    @Resource
    private val userDetailsService: AppUserDetailsService? = null


    @Bean
    @Throws(java.lang.Exception::class)
    fun authenticationManager(authConfig: AuthenticationConfiguration): AuthenticationManager?{
        return authConfig.authenticationManager
    }

    /**
     * BCrypt is default password encoder.
     * */
    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder?{
        return BCryptPasswordEncoder()
    }

    @Bean
    fun authenticationProvider(): DaoAuthenticationProvider{
        val authProvider = DaoAuthenticationProvider()
        authProvider.setUserDetailsService(userDetailsService)
        authProvider.setPasswordEncoder(passwordEncoder())
        return authProvider
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain{
        // No CSRF y cualquier request que cumpla con requisitos definidos en 'authorizeHttpRequests'.
        http
            .csrf{
                it.disable()
            }.cors{
                it.configurationSource(corsConfigurationSource())
            }.authorizeHttpRequests{
                // Permitir cualquier request que va a:
                // 1. URL no segura.
                // 2. O cualquier POST request al SIGNUP endpoint.
                // 3. O cualquier request autenticado.
                it
                    .requestMatchers('/'.plus(URL_UNSECURE!!).plus("/**")).permitAll()
                    .requestMatchers(HttpMethod.POST, URL_SIGNUP).permitAll()
                    .requestMatchers("/**").authenticated()
            }
            .sessionManagement{
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authenticationProvider(authenticationProvider())
            // .apply(customDsl()) // deprecated

            // DSL custom siguiendo ejemplo: https://docs.spring.io/spring-security/reference/servlet/configuration/java.html
            // https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/config/annotation/AbstractConfiguredSecurityBuilder.html#apply(C)
            .with(AppCustomDsl.customDsl(), Customizer.withDefaults())
        // Return filter.
        return http.build()
    }

    /**
     * Configuración CORS a usar.
     */
    @Bean
    fun corsConfigurationSource():CorsConfigurationSource{
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration().apply{
            allowCredentials = true
            addAllowedOrigin("http://localhost:3000")
            addAllowedHeader("*")
            addAllowedMethod("*")
        }

        source.registerCorsConfiguration("/**", config)
        return source
    }
}


class AppCustomDsl : AbstractHttpConfigurer<AppCustomDsl?, HttpSecurity?>() {
    override fun configure(http: HttpSecurity?) {
        super.configure(builder)
        val authenticationManager = http?.getSharedObject(
            AuthenticationManager::class.java
        )

        http?.addFilter(JwtAuthenticationFilter(authenticationManager!!))
        http?.addFilter(JwtAuthorizationFilter(authenticationManager!!))
    }
    companion object {
        fun customDsl(): AppCustomDsl {
            return AppCustomDsl()
        }
    }
}