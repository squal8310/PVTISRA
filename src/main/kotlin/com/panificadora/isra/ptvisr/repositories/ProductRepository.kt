package com.panificadora.isra.ptvisr.repositories

import com.panificadora.isra.ptvisr.models.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : JpaRepository<Product, Long> {
    fun findByNameContainingIgnoreCase(name: String): List<Product>
    fun findByBarcode(barcode: String): List<Product> // New method for barcode search
}
