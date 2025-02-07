package com.una.tango


import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import java.io.IOException
import java.util.*
import javax.crypto.SecretKey
import javax.naming.AuthenticationException

/***
 * This class will hold constants used in the security.
 */
object SecurityConstants{
    // JWT token defaults val TOKEN_PREFIX = "Bearer "
    const val TOKEN_TYPE = "JWT"
    const val TOKEN_ISSUER = "secure-api"
    const val TOKEN_AUDIENCE = "secure-app"
    const val TOKEN_LIFETIME = 864000000 // milisegundos
    const val TOKEN_PREFIX = "Bearer "
    const val APPLICATION_JSON = "application/json"
    const val UTF_8 = "UTF-8"
    val TOKEN_SECRET : String = Base64.getEncoder().encodeToString("4duajWn0gl9E211ATj8BgyA6PkqTdtlk3xjwHa81igSWzmw7ekBr3jt62sOTrB1Y".toByteArray())
}

/***
 *
 */
class JwtAuthenticationFilter(authenticationManager : AuthenticationManager):UsernamePasswordAuthenticationFilter() {
    private val authManager: AuthenticationManager

    init {
        // Filtro Maneja la siguiente URL.
        setFilterProcessesUrl("/api/v1/users/login")
        authManager = authenticationManager
    }

    @Throws(AuthenticationException::class)
    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        // Autenticación solo por POST.
        if(request.method != "POST"){
            throw AuthenticationException("Authentication method not supported: $request.method")
        }
        // Devolver token después de una autenticación correcta.
        return try {
            val userLoginInput: UserLoginInput = ObjectMapper()
                .readValue(request.inputStream, UserLoginInput::class.java)
            authManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    userLoginInput.username,
                    userLoginInput.password,
                    ArrayList()
                )
            )
        } catch (exception: IOException) {
            throw RuntimeException(exception)
        }

    }

    override fun successfulAuthentication(
        request: HttpServletRequest, response: HttpServletResponse,
        filterChain: FilterChain, authentication: Authentication
    ) {

        val authorities = authentication.authorities.joinToString(","){it.toString()}

        val objectMapper = ObjectMapper()
         val token = Jwts.builder()
            .signWith(key(), SignatureAlgorithm.HS512)
          .setHeaderParam("typ", SecurityConstants.TOKEN_TYPE)
            .setIssuer(SecurityConstants.TOKEN_ISSUER)
            .setAudience(SecurityConstants.TOKEN_AUDIENCE)
           .setSubject((authentication.principal as org.springframework.security.core.userdetails.User).username)
             .claim("authorities", authorities)
           .setExpiration(Date(System.currentTimeMillis() + SecurityConstants.TOKEN_LIFETIME))
            .compact()

        // Preparar y enviar respuesta (colocar token en header).
        response.addHeader(HttpHeaders.AUTHORIZATION,SecurityConstants.TOKEN_PREFIX + token)
        val out = response.writer
        response.contentType = SecurityConstants.APPLICATION_JSON
        response.characterEncoding = SecurityConstants.UTF_8
        out.print(objectMapper.writeValueAsString(authentication.principal))
        out.flush()
    }
}

/**
 * This function will return the private key used to sign the token
 */
private fun key(): SecretKey {
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SecurityConstants.TOKEN_SECRET))
}

/**
 * This class will validate the token
 */
class JwtAuthorizationFilter(authenticationManager: AuthenticationManager) :
    BasicAuthenticationFilter(authenticationManager) {

    @Throws(IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest, response: HttpServletResponse,
        filterChain: FilterChain,
    ) {

        var authorizationToken = request.getHeader(HttpHeaders.AUTHORIZATION)

        if (authorizationToken != null && authorizationToken.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            authorizationToken = authorizationToken.replaceFirst(SecurityConstants.TOKEN_PREFIX.toRegex(), "")
            
             val username: String = Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(authorizationToken).body.subject

            LoggedUser.logIn(username)

            SecurityContextHolder.getContext().authentication =
                UsernamePasswordAuthenticationToken(username, null, emptyList())
        }

        filterChain.doFilter(request, response)
    }

}

/**
 * Object to hold the user information
 */
object LoggedUser {
    private val userHolder = ThreadLocal<String>()
    fun logIn(user: String) {
        userHolder.set(user)
    }

    fun logOut() {
        userHolder.remove()
    }

    fun get(): String {
        return userHolder.get()
    }
}