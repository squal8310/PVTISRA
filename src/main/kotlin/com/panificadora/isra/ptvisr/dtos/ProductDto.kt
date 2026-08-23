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
    val stockLimit: Int?,
    val wholesalePrice: Double?, // Added
    val minWholesaleQuantity: Int? // Added
)

fun ProductDto.toFormDto(): ProductFormDto {
    return ProductFormDto(
        id = this.id,
        name = this.name,
        description = this.description,
        price = this.price,
        stock = this.stock,
        categoryId = this.categoryId,
        supplierId = this.supplierId,
        imageUrl = this.imageUrl,
        purchasePrice = this.purchasePrice,
        sku = this.sku,
        barcode = this.barcode,
        unitId = this.unitId,
        stockLimit = this.stockLimit,
        wholesalePrice = this.wholesalePrice,
        minWholesaleQuantity = this.minWholesaleQuantity
    )
}