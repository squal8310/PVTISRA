package com.panificadora.isra.ptvisr.controllers

import com.panificadora.isra.ptvisr.dtos.SaleDto
import com.panificadora.isra.ptvisr.models.Sale
import com.panificadora.isra.ptvisr.services.SaleService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/sales")
class SaleController(
    private val saleService: SaleService
) {

    @PostMapping
    fun processSale(@RequestBody saleDto: SaleDto): ResponseEntity<Any> {
        return try {
            val newSale = saleService.processSale(saleDto)
            ResponseEntity(newSale, HttpStatus.CREATED)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("message" to (e.message ?: "Invalid argument provided")))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf("message" to "Internal server error: ${e.message ?: "Unknown error"}"))
        }
    }
}
