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
import org.springframework.web.multipart.MultipartFile // Added
import java.nio.file.Files // Added
import java.nio.file.Path // Added
import kotlin.io.path.Path // Added

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
            stockLimit = null,
            wholesalePrice = null,
            minWholesaleQuantity = null
        ))
        model.addAttribute("categories", categoryRepository.findAll())
        model.addAttribute("suppliers", supplierRepository.findAll())
        model.addAttribute("unitsOfMeasure", unitOfMeasureRepository.findAll())
        return "product_form"
    }

    @PostMapping("/new")
    fun saveProductAjax(@ModelAttribute productForm: ProductFormDto, model: Model): String {
        // Server-side validation for required fields
        if (productForm.name.isBlank()) {
            model.addAttribute("successMessage", "Proporcione nombre de producto");
            return "layout :: mainPage(page='product_form', fragment='content')"
        }
        if (productForm.price <= Double.MIN_VALUE) {
            model.addAttribute("successMessage", "Precio de venta debe ser mayor a 0");
            return "layout :: mainPage(page='product_form', fragment='content')"
        }
        if (productForm.stock < Double.MIN_VALUE) { // Stock can be zero
            model.addAttribute("successMessage", "Existencia debe ser mayor a 0");
            return "layout :: mainPage(page='product_form', fragment='content')"
        }

        val category = productForm.categoryId?.let { categoryRepository.findById(it).orElse(null) }
        val supplier = productForm.supplierId?.let { supplierRepository.findById(it).orElse(null) }
        val unitOfMeasure = productForm.unitId?.let { unitOfMeasureRepository.findById(it).orElse(null) }

        val product = productForm.toEntity(category, supplier, unitOfMeasure).copy(imageUrl = null) // Ensure imageUrl is null for new products
        val savedProduct = productRepository.save(product)
        model.addAttribute("categories", categoryRepository.findAll())
        model.addAttribute("suppliers", supplierRepository.findAll())
        model.addAttribute("unitsOfMeasure", unitOfMeasureRepository.findAll())
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
            stockLimit = null,
            wholesalePrice = null,
            minWholesaleQuantity = null
        ))
        return "layout :: mainPage(page='product_form', fragment='content')"
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

    @PostMapping("/uploadImage")
    @ResponseBody // This method returns JSON, not a view
    fun uploadProductImage(
        @RequestParam("file") file: MultipartFile,
        @RequestParam("productId") productId: Long,
        @RequestParam("productName") productName: String
    ): Map<String, String> {
        if (file.isEmpty) {
            throw IllegalArgumentException("File is empty")
        }

        // Sanitize product name for filename
        val sanitizedProductName = productName.replace(Regex("[^a-zA-Z0-9.-]"), "_")

        // Determine file extension
        val originalFilename = file.originalFilename ?: "image"
        val fileExtension = originalFilename.substringAfterLast('.', "")
        val fileName = "${productId}_${sanitizedProductName}.${fileExtension}"

        val uploadDir = "C:/ptv/images/" // User specified path
        val uploadPath = Path(uploadDir)

        // Create directory if it doesn't exist
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath)
        }

        val filePath = uploadPath.resolve(fileName)
        file.transferTo(filePath) // Save the file

        // Update product's imageUrl in the database
        var product = productRepository.findById(productId).orElseThrow {
            NoSuchElementException("Product with ID $productId not found")
        }

        // Assuming imageUrl in Product entity stores the relative path or URL
        val imageUrl = "/images/$fileName" // This is the URL that will be served by the web server
        product.imageUrl = imageUrl
        productRepository.save(product)

        return mapOf("message" to "Image uploaded successfully", "fileName" to fileName, "imageUrl" to imageUrl)
    }
}