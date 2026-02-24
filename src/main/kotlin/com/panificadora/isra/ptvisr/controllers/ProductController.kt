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

// Import the new DTOs and mappers
import com.panificadora.isra.ptvisr.dtos.ProductDto
import com.panificadora.isra.ptvisr.dtos.ProductFormDto
import com.panificadora.isra.ptvisr.dtos.toDto
import com.panificadora.isra.ptvisr.dtos.toEntity

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
        model.addAttribute("productForm", ProductFormDto(
            name = "",
            description = null,
            price = Double.MIN_VALUE,
            stock = Double.MIN_VALUE,
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
        return "product_form"
    }

    @PostMapping("/new")
    @ResponseBody
    fun saveProductAjax(@RequestBody productFormDto: ProductFormDto): ResponseEntity<ProductDto> {
        // Server-side validation for required fields
        if (productFormDto.name.isBlank()) {
            return ResponseEntity.badRequest().build() // Or return a more specific error message
        }
        if (productFormDto.price <= Double.MIN_VALUE) {
            return ResponseEntity.badRequest().build() // Or return a more specific error message
        }
        if (productFormDto.stock < Double.MIN_VALUE) { // Stock can be zero
            return ResponseEntity.badRequest().build() // Or return a more specific error message
        }

        val category = productFormDto.categoryId?.let { categoryRepository.findById(it).orElse(null) }
        val supplier = productFormDto.supplierId?.let { supplierRepository.findById(it).orElse(null) }
        val unitOfMeasure = productFormDto.unitId?.let { unitOfMeasureRepository.findById(it).orElse(null) }

        val product = productFormDto.toEntity(category, supplier, unitOfMeasure)
        val savedProduct = productRepository.save(product)
        return ResponseEntity.ok(savedProduct.toDto())
    }

    @GetMapping("/search")
    @ResponseBody
    fun searchProducts(
        @RequestParam(required = false) searchTerm: String?,
        @RequestParam(required = false) barcode: String?
    ): List<ProductDto> {
        val products = if (!searchTerm.isNullOrBlank()) {
            productRepository.findByNameContainingIgnoreCase(searchTerm)
        } else if (!barcode.isNullOrBlank()) {
            productRepository.findByBarcode(barcode)
        } else {
            emptyList()
        }
        return products.map { it.toDto() }
    }
}