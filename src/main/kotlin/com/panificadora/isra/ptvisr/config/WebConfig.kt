package com.panificadora.isra.ptvisr.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {
    
    override fun addCorsMappings(registry: CorsRegistry) {
        // Configuración global deshabilitada para usar @CrossOrigin en controladores específicos
        // registry.addMapping("/**")
        //     .allowedOriginPatterns(
        //         "http://localhost:19006",  // Expo web
        //         "http://localhost:8081",   // Puerto común de Expo
        //         "http://localhost:8080",   // Backend web
        //         "exp://192.168.*:*",       // Expo móvil en red local
        //         "http://192.168.*:*",      // Red local para testing
        //         "http://10.0.2.2:*",       // Emulador Android
        //         "*"                        // Permitir todos los orígenes para desarrollo
        //     )
        //     .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
        //     .allowedHeaders("*")
        //     .allowCredentials(true)
        //     .maxAge(3600)
    }
}