package com.panificadora.isra.ptvisr.controllers

import com.panificadora.isra.ptvisr.dtos.AuthResponse
import com.panificadora.isra.ptvisr.dtos.LoginRequest
import com.panificadora.isra.ptvisr.models.User
import com.panificadora.isra.ptvisr.repositories.UserRepository
import com.panificadora.isra.ptvisr.services.JwtService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = ["*"], allowCredentials = "false")
@Tag(name = "Autenticación", description = "Endpoints para autenticación y gestión de tokens JWT")
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val jwtService: JwtService,
    private val userRepository: UserRepository
) {

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica usuario y retorna token JWT")
    fun authenticateUser(@RequestBody loginRequest: LoginRequest, response: HttpServletResponse): ResponseEntity<AuthResponse> {
        return try {
            val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(loginRequest.username, loginRequest.password)
            )
            
            // Obtener el usuario desde el repositorio
            val user = userRepository.findByUserIdentifier(loginRequest.username)
                ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
            
            // Generar JWT token
            val token = jwtService.generateToken(user)
            
            // Crear cookie HttpOnly para la web
            val cookie = ResponseCookie.from("jwt_token", token)
                .httpOnly(true)
                .secure(false) // true en producción con HTTPS
                .path("/")
                .maxAge(24 * 60 * 60) // 24 horas
                .sameSite("Strict")
                .build()
            
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())
            
            // Crear respuesta con token y datos del usuario (para app móvil)
            val authResponse = AuthResponse(
                token = token,
                user = com.panificadora.isra.ptvisr.dtos.UserInfo(
                    id = user.id,
                    username = user.userIdentifier,
                    role = user.role.name
                )
            )
            
            ResponseEntity.ok(authResponse)
        } catch (e: AuthenticationException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }
}
