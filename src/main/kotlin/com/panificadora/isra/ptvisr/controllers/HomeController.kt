package com.panificadora.isra.ptvisr.controllers

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Controller
class HomeController {

    @GetMapping("/") // Changed to root path
    fun home(model: Model): String {
        val infoPing = "Bienvenido al sistema POS e Inventario. Fecha: "+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        model.addAttribute("infoPing", infoPing);
        return "layout"; // Changed view name to layout
    }

    @GetMapping("/pos")
    fun pointOfSale(): String {
        return "point_of_sale"
    }
}