package com.panificadora.isra.ptvisr.controllers

import com.panificadora.isra.ptvisr.models.Product
import com.panificadora.isra.ptvisr.repositories.CategoryRepository
import com.panificadora.isra.ptvisr.repositories.ProductRepository
import com.panificadora.isra.ptvisr.repositories.SupplierRepository
import com.panificadora.isra.ptvisr.repositories.UnitOfMeasureRepository
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.http.ResponseEntity
import java.math.BigDecimal

@Controller
@RequestMapping("/products")
class ProductController(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val supplierRepository: SupplierRepository,
    private val unitOfMeasureRepository: UnitOfMeasureRepository
) {

    @GetMapping("/new")
    fun showProductForm(model: Model): String {
        model.addAttribute("product", Product(
            name = "",
            description = null,
            price = BigDecimal.ZERO,
            stock = BigDecimal.ZERO,
            category = null,
            categoryId = null,
            supplier = null,
            supplierId = null,
            imageUrl = null,
            purchasePrice = null,
            sku = null,
            barcode = null,
            unit = null,
            unitId = null
        ))
        model.addAttribute("categories", categoryRepository.findAll())
        model.addAttribute("suppliers", supplierRepository.findAll())
        model.addAttribute("unitsOfMeasure", unitOfMeasureRepository.findAll())
        return "product_form"
    }

    @PostMapping("/new")
    @ResponseBody
    fun saveProductAjax(@RequestBody product: Product): ResponseEntity<Product> {
        // Server-side validation for required fields
        if (product.name.isBlank()) {
            return ResponseEntity.badRequest().build() // Or return a more specific error message
        }
        if (product.price == null || product.price <= BigDecimal.ZERO) {
            return ResponseEntity.badRequest().build() // Or return a more specific error message
        }
        if (product.stock == null || product.stock < BigDecimal.ZERO) { // Stock can be zero
            return ResponseEntity.badRequest().build() // Or return a more specific error message
        }

        product.categoryId?.let {
            product.category = categoryRepository.findById(it).orElse(null)
        }
        product.supplierId?.let {
            product.supplier = supplierRepository.findById(it).orElse(null)
        }
        product.unitId?.let {
            product.unit = unitOfMeasureRepository.findById(it).orElse(null)
        }

        val savedProduct = productRepository.save(product)
        return ResponseEntity.ok(savedProduct)
    }

    @GetMapping("/search")
    @ResponseBody
    fun searchProducts(
        @RequestParam(required = false) searchTerm: String?,
        @RequestParam(required = false) barcode: String?
    ): List<Product> {
        if (!searchTerm.isNullOrBlank()) {
            return productRepository.findByNameContainingIgnoreCase(searchTerm)
        }
        if (!barcode.isNullOrBlank()) {
            return productRepository.findByBarcode(barcode)
        }
        return emptyList()
    }
}
