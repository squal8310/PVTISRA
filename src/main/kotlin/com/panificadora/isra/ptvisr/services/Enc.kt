package com.panificadora.isra.ptvisr.services

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class Enc {

    fun main() {
        val passwordEncoder = BCryptPasswordEncoder()
        val myPassword = "test123" // Cambia esto por la
        println("Contraseña encriptada para '$myPassword': ${passwordEncoder.encode(myPassword)}")
        7     }
}