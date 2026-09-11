package com.panificadora.isra.ptvisr.config

import com.panificadora.isra.ptvisr.services.JwtService
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val userDetailsService: UserDetailsService
) : OncePerRequestFilter() {
    
    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
    }
    
    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        var token: String? = null
        
        // Primero intentar obtener token del header Authorization (app móvil)
        val authHeader = request.getHeader(AUTHORIZATION_HEADER)
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            token = authHeader.substring(BEARER_PREFIX.length)
        }
        
        // Si no hay token en header, intentar obtener de cookie (app web)
        if (token == null) {
            val cookies = request.cookies
            if (cookies != null) {
                for (cookie in cookies) {
                    if (cookie.name == "jwt_token") {
                        token = cookie.value
                        break
                    }
                }
            }
        }
        
        // Si no hay token en ningún lugar, continuar sin autenticación
        if (token == null) {
            filterChain.doFilter(request, response)
            return
        }
        
        val username = jwtService.extractUsername(token)
        
        if (username != null && SecurityContextHolder.getContext().authentication == null) {
            if (jwtService.validateToken(token)) {
                val userDetails: UserDetails = userDetailsService.loadUserByUsername(username)
                
                val authentication = UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.authorities
                )
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                
                SecurityContextHolder.getContext().authentication = authentication
            }
        }
        
        filterChain.doFilter(request, response)
    }
}