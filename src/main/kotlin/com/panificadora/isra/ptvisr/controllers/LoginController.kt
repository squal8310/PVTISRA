package com.panificadora.isra.ptvisr.controllers

import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping

@Controller
class LoginController {

    @GetMapping("/login")
    fun loginPage(): String {
        return "login"
    }

    @GetMapping("/logout")
    fun logout(response: HttpServletResponse): String {
        // Eliminar la cookie JWT
        val cookie = ResponseCookie.from("jwt_token", "")
            .httpOnly(true)
            .secure(false) // true en producción con HTTPS
            .path("/")
            .maxAge(0) // Expirar inmediatamente
            .sameSite("Strict")
            .build()
        
        response.addHeader("Set-Cookie", cookie.toString())
        
        // Redirigir a la página de login
        return "redirect:/login"
    }
}