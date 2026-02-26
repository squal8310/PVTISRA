package com.panificadora.isra.ptvisr.dtos

import com.panificadora.isra.ptvisr.models.Product
import com.panificadora.isra.ptvisr.models.Category
import com.panificadora.isra.ptvisr.models.Supplier
import com.panificadora.isra.ptvisr.models.UnitOfMeasure

fun Product.toDto(): ProductDto {
    return ProductDto(
        id = this.id,
        name = this.name,
        description = this.description,
        price = this.price,
        stock = this.stock,
        categoryId = this.categoryId,
        categoryName = this.category?.name,
        supplierId = this.supplierId,
        supplierName = this.supplier?.name,
        imageUrl = this.imageUrl,
        purchasePrice = this.purchasePrice,
        sku = this.sku,
        barcode = this.barcode,
        unitId = this.unitId,
        unitName = this.unit?.name,
        stockLimit = this.stockLimit,
        wholesalePrice = this.wholesalePrice, // Added
        minWholesaleQuantity = this.minWholesaleQuantity // Added
    )
}

fun ProductFormDto.toEntity(
    category: Category?,
    supplier: Supplier?,
    unitOfMeasure: UnitOfMeasure?
): Product {
    return Product(
        id = this.id,
        name = this.name,
        description = this.description,
        price = this.price,
        stock = this.stock,
        category = category,
        categoryId = this.categoryId,
        supplier = supplier,
        supplierId = this.supplierId,
        imageUrl = this.imageUrl,
        purchasePrice = this.purchasePrice,
        sku = this.sku,
        barcode = this.barcode,
        unit = unitOfMeasure,
        unitId = this.unitId,
        stockLimit = this.stockLimit,
        wholesalePrice = this.wholesalePrice, // Added
        minWholesaleQuantity = this.minWholesaleQuantity // Added
    )
}