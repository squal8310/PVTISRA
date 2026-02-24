package com.panificadora.isra.ptvisr

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@SpringBootApplication
class PtvisrApplication

fun main(args: Array<String>) {
	val passwordEncoder = BCryptPasswordEncoder()
	val myPassword = "test123" // Cambia esto por la
	val encode = passwordEncoder.encode(myPassword)
	println("Contraseña encriptada para '$myPassword': $encode")
	runApplication<PtvisrApplication>(*args)
}
