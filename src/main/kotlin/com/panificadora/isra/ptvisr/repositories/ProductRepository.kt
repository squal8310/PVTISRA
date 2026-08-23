package com.panificadora.isra.ptvisr.repositories

import com.panificadora.isra.ptvisr.models.Product
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : JpaRepository<Product, Long> {
    fun findByNameContainingIgnoreCase(name: String, pageable: Pageable): Page<Product>
    fun findByBarcode(barcode: String, pageable: Pageable): Page<Product>
    override fun findAll(pageable: Pageable): Page<Product> // Explicitly define for clarity, though JpaRepository has it
}
