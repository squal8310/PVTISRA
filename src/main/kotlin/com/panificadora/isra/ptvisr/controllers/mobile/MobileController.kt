package com.panificadora.isra.ptvisr.controllers.mobile

import com.panificadora.isra.ptvisr.dtos.DashboardDto
import com.panificadora.isra.ptvisr.dtos.MobileProductDto
import com.panificadora.isra.ptvisr.dtos.MobileSaleDto
import com.panificadora.isra.ptvisr.dtos.toMobileDto
import com.panificadora.isra.ptvisr.models.Product
import com.panificadora.isra.ptvisr.repositories.ProductRepository
import com.panificadora.isra.ptvisr.services.DashboardService
import com.panificadora.isra.ptvisr.services.SaleService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/mobile")
@CrossOrigin(origins = ["*"], allowCredentials = "false")
class MobileController(
    private val productRepository: ProductRepository,
    private val dashboardService: DashboardService,
    private val saleService: SaleService
) {
    
    // === PRODUCTOS ===
    
    @GetMapping("/products")
    fun getProducts(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) search: String?,
        @RequestParam(required = false) barcode: String?
    ): ResponseEntity<Map<String, Any>> {
        val pageable: Pageable = PageRequest.of(page, size)
        val productsPage: Page<Product> = when {
            !search.isNullOrBlank() -> productRepository.findByNameContainingIgnoreCaseWithRelations(search, pageable)
            !barcode.isNullOrBlank() -> productRepository.findByBarcodeWithRelations(barcode, pageable)
            else -> productRepository.findAllWithRelations(pageable)
        }
        
        val mobileProducts = productsPage.content.map { it.toMobileDto() }
        
        val response = mapOf(
            "content" to mobileProducts,
            "totalPages" to productsPage.totalPages,
            "number" to productsPage.number,
            "size" to productsPage.size,
            "hasPrevious" to productsPage.hasPrevious(),
            "hasNext" to productsPage.hasNext(),
            "totalElements" to productsPage.totalElements
        )
        
        return ResponseEntity.ok(response)
    }
    
    @GetMapping("/products/{id}")
    fun getProduct(@PathVariable id: Long): ResponseEntity<MobileProductDto> {
        val product = productRepository.findById(id)
        return if (product.isPresent) {
            ResponseEntity.ok(product.get().toMobileDto())
        } else {
            ResponseEntity.notFound().build()
        }
    }
    
    @GetMapping("/products/low-stock")
    fun getLowStockProducts(): ResponseEntity<List<MobileProductDto>> {
        val lowStockProducts = productRepository.findAll().filter { product ->
            val stockLimitValue = product.stockLimit
            stockLimitValue != null && product.stock <= stockLimitValue && product.stock > 0
        }.map { it.toMobileDto() }
        return ResponseEntity.ok(lowStockProducts)
    }
    
    @PostMapping("/products")
    fun createProduct(@RequestBody productDto: MobileProductDto): ResponseEntity<MobileProductDto> {
        // Implementación básica - se puede expandir con más campos
        val newProduct = Product(
            name = productDto.name,
            description = null,
            price = productDto.price,
            stock = productDto.stock,
            category = null,
            categoryId = null,
            supplier = null,
            supplierId = null,
            imageUrl = null,
            purchasePrice = null,
            sku = null,
            barcode = productDto.barcode,
            unit = null,
            unitId = null,
            stockLimit = productDto.stockLimit?.toInt(),
            wholesalePrice = null,
            minWholesaleQuantity = null
        )
        val savedProduct = productRepository.save(newProduct)
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct.toMobileDto())
    }
    
    @PutMapping("/products/{id}")
    fun updateProduct(
        @PathVariable id: Long,
        @RequestBody productDto: MobileProductDto
    ): ResponseEntity<MobileProductDto> {
        val existingProduct = productRepository.findById(id)
        return if (existingProduct.isPresent) {
            val product = existingProduct.get()
            // Product es un data class, por lo que necesitamos crear una nueva instancia
            val updatedProduct = product.copy(
                name = productDto.name,
                price = productDto.price,
                stock = productDto.stock,
                barcode = productDto.barcode,
                stockLimit = productDto.stockLimit?.toInt()
            )
            val savedProduct = productRepository.save(updatedProduct)
            ResponseEntity.ok(savedProduct.toMobileDto())
        } else {
            ResponseEntity.notFound().build()
        }
    }
    
    @DeleteMapping("/products/{id}")
    fun deleteProduct(@PathVariable id: Long): ResponseEntity<Void> {
        return if (productRepository.existsById(id)) {
            productRepository.deleteById(id)
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
    
    // === VENTAS ===
    
    @PostMapping("/sales")
    fun createSale(@RequestBody saleDto: MobileSaleDto): ResponseEntity<Map<String, Any>> {
        return try {
            // Convertir MobileSaleDto a SaleDto existente
            val saleDetails = saleDto.items.map { item ->
                com.panificadora.isra.ptvisr.dtos.SaleDetailDto(
                    productId = item.productId,
                    quantity = item.quantity,
                    unitPrice = item.price
                )
            }
            
            val internalSaleDto = com.panificadora.isra.ptvisr.dtos.SaleDto(
                details = saleDetails
            )
            
            val sale = saleService.processSale(internalSaleDto)
            
            val response: Map<String, Any> = mapOf(
                "id" to sale.id,
                "total" to sale.totalAmount,
                "saleDate" to sale.saleDate.toString(),
                "message" to "Venta registrada exitosamente"
            )
            
            ResponseEntity(response, HttpStatus.CREATED)
        } catch (e: Exception) {
            val errorResponse: Map<String, Any> = mapOf(
                "error" to (e.message ?: "Error desconocido")
            )
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
        }
    }
    
    // === DASHBOARD ===
    
    @GetMapping("/dashboard")
    fun getDashboard(): ResponseEntity<DashboardDto> {
        return ResponseEntity.ok(dashboardService.getDashboardData())
    }
    
    // === REPORTES ===
    
    @GetMapping("/sales/daily/{date}")
    @CrossOrigin(origins = ["*"], allowCredentials = "false")
    fun getDailyReport(@PathVariable date: String): ResponseEntity<Map<String, Any>> {
        return try {
            val localDate = LocalDate.parse(date)
            val startDate = localDate.atStartOfDay()
            val endDate = localDate.atTime(23, 59, 59)
            
            // Aquí se implementaría la lógica de reportes diarios
            // Por ahora retornamos una estructura básica
            val response: Map<String, Any> = mapOf(
                "date" to date,
                "message" to "Reporte diario no implementado completamente"
            )
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            val errorResponse: Map<String, Any> = mapOf(
                "error" to "Formato de fecha inválido. Use YYYY-MM-DD"
            )
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
        }
    }
    
    @GetMapping("/sales/monthly/{year}/{month}")
    @CrossOrigin(origins = ["*"], allowCredentials = "false")
    fun getMonthlyReport(
        @PathVariable year: Int,
        @PathVariable month: Int
    ): ResponseEntity<Map<String, Any>> {
        return try {
            // Aquí se implementaría la lógica de reportes mensuales
            // Por ahora retornamos una estructura básica
            val response: Map<String, Any> = mapOf(
                "year" to year,
                "month" to month,
                "message" to "Reporte mensual no implementado completamente"
            )
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            val errorResponse: Map<String, Any> = mapOf(
                "error" to (e.message ?: "Error desconocido")
            )
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
        }
    }
}