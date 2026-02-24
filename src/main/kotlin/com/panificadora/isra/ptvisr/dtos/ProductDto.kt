package com.panificadora.isra.ptvisr.dtos

data class ProductDto(
    val id: Long,
    val name: String,
    val description: String?,
    val price: Double,
    val stock: Double,
    val categoryId: Int?,
    val categoryName: String?,
    val supplierId: Long?,
    val supplierName: String?,
    val imageUrl: String?,
    val purchasePrice: Double?,
    val sku: String?,
    val barcode: String?,
    val unitId: Int?,
    val unitName: String?,
    val stockLimit: Int?
)
