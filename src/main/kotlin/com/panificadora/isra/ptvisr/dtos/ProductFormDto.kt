package com.panificadora.isra.ptvisr.dtos

data class ProductFormDto(
    val id: Long = 0,
    val name: String,
    val description: String?,
    val price: Double,
    val stock: Double,
    val categoryId: Int?,
    val supplierId: Long?,
    val imageUrl: String?,
    val purchasePrice: Double?,
    val sku: String?,
    val barcode: String?,
    val unitId: Int?,
    val stockLimit: Int?,
    val wholesalePrice: Double?, // Added
    val minWholesaleQuantity: Int? // Added
)