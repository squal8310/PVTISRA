package com.panificadora.isra.ptvisr.repositories

import com.panificadora.isra.ptvisr.models.Product
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : JpaRepository<Product, Long> {
    
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.supplier LEFT JOIN FETCH p.unit")
    fun findAllWithRelations(pageable: Pageable): Page<Product>
    
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.supplier LEFT JOIN FETCH p.unit WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    fun findByNameContainingIgnoreCaseWithRelations(name: String, pageable: Pageable): Page<Product>
    
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.supplier LEFT JOIN FETCH p.unit WHERE p.barcode = :barcode")
    fun findByBarcodeWithRelations(barcode: String, pageable: Pageable): Page<Product>
    
    // Mantener los métodos originales para compatibilidad
    fun findByNameContainingIgnoreCase(name: String, pageable: Pageable): Page<Product>
    fun findByBarcode(barcode: String, pageable: Pageable): Page<Product>
    override fun findAll(pageable: Pageable): Page<Product>
    
    @Query("SELECT p FROM Product p WHERE NOT EXISTS (SELECT 1 FROM SaleDetail sd WHERE sd.product = p)")
    fun findProductsNeverSold(): List<Product>
}
