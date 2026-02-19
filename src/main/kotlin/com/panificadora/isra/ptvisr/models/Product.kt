package com.panificadora.isra.ptvisr.models

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "products")
data class Product(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String,
    val description: String?,
    val price: BigDecimal,
    var stock: BigDecimal,
    @ManyToOne @JoinColumn(name = "category_id", insertable = false, updatable = false)
    var category: Category?,
    @Column(name = "category_id")
    var categoryId: Int?,
    @ManyToOne @JoinColumn(name = "supplier_id", insertable = false, updatable = false)
    var supplier: Supplier?,
    @Column(name = "supplier_id")
    var supplierId: Long?,
    @Column(name = "image_url")
    val imageUrl: String?,
    @Column(name = "purchase_price")
    val purchasePrice: BigDecimal?,
    val sku: String?,
    val barcode: String?,
    @ManyToOne @JoinColumn(name = "unit_id", insertable = false, updatable = false)
    var unit: UnitOfMeasure?,
    @Column(name = "unit_id")
    var unitId: Int?
)
