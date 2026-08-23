package com.panificadora.isra.ptvisr.controllers

import com.panificadora.isra.ptvisr.models.Product
import com.panificadora.isra.ptvisr.repositories.CategoryRepository
import com.panificadora.isra.ptvisr.repositories.ProductRepository
import com.panificadora.isra.ptvisr.repositories.SupplierRepository
import com.panificadora.isra.ptvisr.repositories.UnitOfMeasureRepository
import org.springframework.data.domain.Page // Added
import org.springframework.data.domain.PageRequest // Added
import org.springframework.data.domain.Pageable // Added
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.http.ResponseEntity
import org.springframework.web.multipart.MultipartFile // Added
import java.nio.file.Files // Added
import java.nio.file.Path // Added
import kotlin.io.path.Path // Added

// Import the new DTOs and mappers
import com.panificadora.isra.ptvisr.dtos.ProductFormDto
import com.panificadora.isra.ptvisr.dtos.toDto
import com.panificadora.isra.ptvisr.dtos.toEntity
import com.panificadora.isra.ptvisr.dtos.toFormDto

@Controller
@RequestMapping("/products")
class ProductController(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val supplierRepository: SupplierRepository,
    private val unitOfMeasureRepository: UnitOfMeasureRepository
) {

    // Shows the product list page (initial load, data is fetched via AJAX)
    @GetMapping
    fun showProductList(model: Model): String {
        return "layout :: mainPage(page='product_form', fragment='content')"
    }

    // AJAX endpoint — returns product page as JSON for dynamic search/pagination
    @GetMapping("/search")
    @ResponseBody
    fun searchProducts(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) searchTerm: String?,
        @RequestParam(required = false) barcode: String?
    ): ResponseEntity<Map<String, Any>> {
        val pageable: Pageable = PageRequest.of(page, size)
        val productsPage = when {
            !searchTerm.isNullOrBlank() -> productRepository.findByNameContainingIgnoreCase(searchTerm, pageable)
            !barcode.isNullOrBlank()    -> productRepository.findByBarcode(barcode, pageable)
            else                        -> productRepository.findAll(pageable)
        }
        val contentWithStatus = productsPage.content.map { product ->
            val dto = product.toDto()
            val missingFields = mutableListOf<String>()
            if (product.imageUrl.isNullOrBlank()) missingFields.add("imagen")
            if (product.wholesalePrice == null) missingFields.add("mayoreo")
            if (product.stockLimit == null) missingFields.add("alerta")
            mapOf(
                "id" to dto.id,
                "name" to dto.name,
                "price" to dto.price,
                "stock" to dto.stock,
                "categoryName" to dto.categoryName,
                "supplierName" to dto.supplierName,
                "imageUrl" to dto.imageUrl,
                "wholesalePrice" to dto.wholesalePrice,
                "stockLimit" to dto.stockLimit,
                "isIncomplete" to missingFields.isNotEmpty(),
                "missingFields" to missingFields
            )
        }
        val response: Map<String, Any> = mapOf(
            "content"     to contentWithStatus,
            "totalPages"  to productsPage.totalPages,
            "number"      to productsPage.number,
            "size"        to productsPage.size,
            "hasPrevious" to productsPage.hasPrevious(),
            "hasNext"     to productsPage.hasNext()
        )
        return ResponseEntity.ok(response)
    }

    // Method to show the form for adding a new product
    @GetMapping("/new")
    fun showAddProductForm(model: Model): String {
        model.addAttribute("productForm", ProductFormDto(
            name = "", description = null, price = 0.0, stock = 0.0, categoryId = null, supplierId = null,
            imageUrl = null, purchasePrice = null, sku = null, barcode = null, unitId = null, stockLimit = null,
            wholesalePrice = null, minWholesaleQuantity = null
        ))
        model.addAttribute("isCreating", true)
        model.addAttribute("categories", categoryRepository.findAll())
        model.addAttribute("suppliers", supplierRepository.findAll())
        model.addAttribute("unitsOfMeasure", unitOfMeasureRepository.findAll())
        return "layout :: mainPage(page='product_form_create', fragment='content')"
    }

    @PostMapping("/new")
    fun saveProduct(
        @ModelAttribute productForm: ProductFormDto,
        model: Model
    ): String {
        // Re-populate model attributes for error cases
        fun populateModelForErrors() {
            model.addAttribute("categories", categoryRepository.findAll())
            model.addAttribute("suppliers", supplierRepository.findAll())
            model.addAttribute("unitsOfMeasure", unitOfMeasureRepository.findAll())
            model.addAttribute("productForm", productForm)
        }

        // Server-side validation for required fields
        if (productForm.name.isBlank()) {
            model.addAttribute("errorMessage", "Proporcione nombre de producto")
            populateModelForErrors()
            return "layout :: mainPage(page='product_form_create', fragment='content')"
        }
        if (productForm.price <= 0) {
            model.addAttribute("errorMessage", "Precio de venta debe ser mayor a 0")
            populateModelForErrors()
            return "layout :: mainPage(page='product_form_create', fragment='content')"
        }
        if (productForm.stock < 0) {
            model.addAttribute("errorMessage", "Existencia debe ser mayor a 0")
            populateModelForErrors()
            return "layout :: mainPage(page='product_form_create', fragment='content')"
        }

        try {
            val category = productForm.categoryId?.let { categoryRepository.findById(it).orElse(null) }
            val supplier = productForm.supplierId?.let { supplierRepository.findById(it).orElse(null) }
            val unitOfMeasure = productForm.unitId?.let { unitOfMeasureRepository.findById(it).orElse(null) }

            val productToSave = productForm.toEntity(category, supplier, unitOfMeasure).copy(imageUrl = null)
            val savedProduct = productRepository.save(productToSave)

            model.addAttribute("successMessage", "¡Producto '${savedProduct.name}' guardado! Ahora puedes editar para agregar imagen y más detalles.")
            model.addAttribute("productForm", ProductFormDto(
                name = "", description = null, price = 0.0, stock = 0.0, categoryId = null, supplierId = null,
                imageUrl = null, purchasePrice = null, sku = null, barcode = null, unitId = null, stockLimit = null,
                wholesalePrice = null, minWholesaleQuantity = null
            ))
            populateModelForErrors()
            return "layout :: mainPage(page='product_form_create', fragment='content')"
        } catch (e: Exception) {
            model.addAttribute("errorMessage", "Error al guardar el producto: ${e.message}")
            populateModelForErrors()
            return "layout :: mainPage(page='product_form_create', fragment='content')"
        }
    }

    // Show edit form pre-filled with existing product data
    @GetMapping("/edit/{id}")
    fun showEditProductForm(@PathVariable id: Long, model: Model): String {
        val product = productRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Producto no encontrado: $id") }
        model.addAttribute("productForm", product.toDto().toFormDto())
        model.addAttribute("categories", categoryRepository.findAll())
        model.addAttribute("suppliers", supplierRepository.findAll())
        model.addAttribute("unitsOfMeasure", unitOfMeasureRepository.findAll())
        return "layout :: mainPage(page='product_form_create', fragment='content')"
    }

    @PostMapping("/edit/{id}")
    fun updateProduct(
        @PathVariable id: Long,
        @ModelAttribute productForm: ProductFormDto,
        @RequestParam("productImage") file: MultipartFile?,
        model: Model
    ): String {
        fun populateModelForErrors() {
            model.addAttribute("categories", categoryRepository.findAll())
            model.addAttribute("suppliers", supplierRepository.findAll())
            model.addAttribute("unitsOfMeasure", unitOfMeasureRepository.findAll())
            model.addAttribute("productForm", productForm)
        }

        val MAX_FILE_SIZE = 10 * 1024 * 1024L // 10 MB

        try {
            if (productForm.name.isBlank()) {
                model.addAttribute("errorMessage", "Proporcione nombre de producto")
                populateModelForErrors()
                return "layout :: mainPage(page='product_form_create', fragment='content')"
            }
            if (productForm.price <= 0) {
                model.addAttribute("errorMessage", "Precio de venta debe ser mayor a 0")
                populateModelForErrors()
                return "layout :: mainPage(page='product_form_create', fragment='content')"
            }
            if (file != null && !file.isEmpty && file.size > MAX_FILE_SIZE) {
                val fileSizeMB = String.format("%.2f", file.size / (1024.0 * 1024.0))
                model.addAttribute("errorMessage", "El archivo ($fileSizeMB MB) excede el tamaño máximo permitido de 10 MB.")
                populateModelForErrors()
                return "layout :: mainPage(page='product_form_create', fragment='content')"
            }
        } catch (e: Exception) {
            model.addAttribute("errorMessage", "Error validando archivo: ${e.message}")
            populateModelForErrors()
            return "layout :: mainPage(page='product_form_create', fragment='content')"
        }

        try {
            val existing = productRepository.findById(id)
                .orElseThrow { IllegalArgumentException("Producto no encontrado: $id") }
            val category     = productForm.categoryId?.let { categoryRepository.findById(it).orElse(null) }
            val supplier     = productForm.supplierId?.let { supplierRepository.findById(it).orElse(null) }
            val unitOfMeasure = productForm.unitId?.let { unitOfMeasureRepository.findById(it).orElse(null) }

            var updatedProduct = productForm.toEntity(category, supplier, unitOfMeasure)
                .copy(id = id, imageUrl = existing.imageUrl)

            if (file != null && !file.isEmpty) {
                val categoryName = category?.name?.replace(Regex("[^a-zA-Z0-9]"), "") ?: "sin_categoria"
                val skuPart      = (productForm.sku ?: "").replace(Regex("[^a-zA-Z0-9]"), "").takeIf { it.isNotEmpty() } ?: "nsku"
                val ext          = (file.originalFilename ?: "image").substringAfterLast('.', "")
                val fileName     = "${id}_${categoryName}_${skuPart}.${ext}"
                val uploadPath   = Path("C:/ptv/images/")

                if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath)
                file.transferTo(uploadPath.resolve(fileName))
                updatedProduct = updatedProduct.copy(imageUrl = "/images/$fileName")
            }

            productRepository.save(updatedProduct)
            return "redirect:/inventory/products"
        } catch (e: Exception) {
            model.addAttribute("errorMessage", "Error al actualizar el producto: ${e.message}")
            populateModelForErrors()
            return "layout :: mainPage(page='product_form_create', fragment='content')"
        }
    }

    @PostMapping("/delete/{id}")
    fun deleteProduct(@PathVariable id: Long): String {
        productRepository.deleteById(id)
        return "redirect:/inventory/products"
    }
}