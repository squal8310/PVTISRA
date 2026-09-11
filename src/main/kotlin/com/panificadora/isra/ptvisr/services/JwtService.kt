package com.panificadora.isra.ptvisr.services

import com.panificadora.isra.ptvisr.models.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import java.security.Key
import java.util.Date

@Service
class JwtService {
    
    companion object {
        // Secret key para firmar tokens (en producción debería venir de variables de entorno)
        private val SECRET_KEY = "miClaveSecretaMuyLargaYSeguraParaFirmarTokensJWT1234567890"
        private val KEY: Key = Keys.hmacShaKeyFor(SECRET_KEY.toByteArray())
        
        // Tiempo de expiración: 24 horas para mobile
        private const val EXPIRATION_TIME: Long = 24 * 60 * 60 * 1000 // 24 horas en milisegundos
    }
    
    /**
     * Genera un token JWT para un usuario
     */
    fun generateToken(user: User): String {
        val now = Date()
        val expiryDate = Date(now.time + EXPIRATION_TIME)
        
        return Jwts.builder()
            .setSubject(user.userIdentifier)
            .claim("userId", user.id)
            .claim("role", user.role.name)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(KEY)
            .compact()
    }
    
    /**
     * Valida si un token es válido
     */
    fun validateToken(token: String): Boolean {
        return try {
            val claims = extractClaims(token)
            val expiration = claims.expiration
            expiration.after(Date())
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Extrae el username (userIdentifier) del token
     */
    fun extractUsername(token: String): String? {
        return try {
            extractClaims(token).subject
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Extrae el ID del usuario del token
     */
    fun extractUserId(token: String): Int? {
        return try {
            val userId = extractClaims(token).get("userId", Integer::class.java)
            userId?.toInt()
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Extrae el rol del usuario del token
     */
    fun extractRole(token: String): String? {
        return try {
            extractClaims(token).get("role", String::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Extrae todos los claims del token
     */
    private fun extractClaims(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(KEY)
            .build()
            .parseClaimsJws(token)
            .body
    }
    
    /**
     * Verifica si el token está próximo a expirar (menos de 1 hora)
     */
    fun isTokenExpiringSoon(token: String): Boolean {
        return try {
            val claims = extractClaims(token)
            val expiration = claims.expiration
            val oneHourFromNow = Date(System.currentTimeMillis() + 60 * 60 * 1000)
            expiration.before(oneHourFromNow)
        } catch (e: Exception) {
            true
        }
    }
}