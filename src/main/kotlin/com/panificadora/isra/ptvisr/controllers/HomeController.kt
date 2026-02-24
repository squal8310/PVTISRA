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
        model.addAttribute("pageTitle", "Dashboard"); // Add pageTitle for the layout
        return "layout :: mainPage(page='dashboard', fragment='content')";
    }

    @GetMapping("/pos")
    fun pointOfSale(): String {
        return "layout :: mainPage(page='point_of_sale', fragment='content')"
    }

    @GetMapping("/inventory/products")
    fun productList(): String {
        return "layout :: mainPage(page='product_form', fragment='content')"
    }

    @GetMapping("/purchases/page")
    fun purchasesNew(): String {
        return "layout :: mainPage(page='purchase_new', fragment='content')"
    }

}
