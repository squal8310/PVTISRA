package com.panificadora.isra.ptvisr.models

import jakarta.persistence.*

@Entity
@Table(name = "products")
data class  Product(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String,
    val description: String?,
    val price: Double,
    var stock: Double,
    @ManyToOne @JoinColumn(name = "category_id", insertable = false, updatable = false)
    var category: Category?,
    @Column(name = "category_id")
    var categoryId: Int?,
    @ManyToOne @JoinColumn(name = "supplier_id", insertable = false, updatable = false)
    var supplier: Supplier?,
    @Column(name = "supplier_id")
    var supplierId: Long?,
    @Column(name = "image_url")
    var imageUrl: String?,
    @Column(name = "purchase_price")
    val purchasePrice: Double?,
    val sku: String?,
    val barcode: String?,
    @ManyToOne @JoinColumn(name = "unit_id", insertable = false, updatable = false)
    var unit: UnitOfMeasure?,
    @Column(name = "unit_id")
    var unitId: Int?,
    @Column(name = "stock_limit")
    var stockLimit: Int?,
    @Column(name = "wholesale_price")
    var wholesalePrice: Double?,
    @Column(name = "min_wholesale_quantity")
    var minWholesaleQuantity: Int?
)
