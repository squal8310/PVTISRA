package com.panificadora.isra.ptvisr.dtos

import com.panificadora.isra.ptvisr.models.Product

data class MobileProductDto(
    val id: Long,
    val name: String,
    val price: Double,
    val stock: Double,
    val barcode: String?,
    val categoryName: String?,
    val supplierName: String?,
    val stockLimit: Double?,
    val isLowStock: Boolean
)

// Función de extensión para convertir Product a MobileProductDto
fun Product.toMobileDto(): MobileProductDto {
    val stockLimitValue = this.stockLimit
    return MobileProductDto(
        id = this.id,
        name = this.name,
        price = this.price.toDouble(),
        stock = this.stock.toDouble(),
        barcode = this.barcode,
        categoryName = this.category?.name,
        supplierName = this.supplier?.name,
        stockLimit = stockLimitValue?.toDouble(),
        isLowStock = stockLimitValue != null && this.stock <= stockLimitValue
    )
}