package com.panificadora.isra.ptvisr.controllers

import com.panificadora.isra.ptvisr.dtos.ProductFormDto
import com.panificadora.isra.ptvisr.repositories.CategoryRepository
import com.panificadora.isra.ptvisr.repositories.SupplierRepository
import com.panificadora.isra.ptvisr.repositories.UnitOfMeasureRepository
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Controller
class HomeController(private val categoryRepository: CategoryRepository,
                     private val supplierRepository: SupplierRepository,
                     private val unitOfMeasureRepository: UnitOfMeasureRepository) {

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
    fun productList(model: Model): String {
        model.addAttribute("productForm", ProductFormDto(
            name = "",
            description = null,
            price = 0.0,
            stock = 0.0,
            categoryId = null,
            supplierId = null,
            imageUrl = null,
            purchasePrice = null,
            sku = null,
            barcode = null,
            unitId = null,
            stockLimit = null
        ))
        model.addAttribute("categories", categoryRepository.findAll())
        model.addAttribute("suppliers", supplierRepository.findAll())
        model.addAttribute("unitsOfMeasure", unitOfMeasureRepository.findAll())
        return "layout :: mainPage(page='product_form', fragment='content')"
    }

    @GetMapping("/purchases/page")
    fun purchasesNew(): String {
        return "layout :: mainPage(page='purchase_new', fragment='content')"
    }
}
