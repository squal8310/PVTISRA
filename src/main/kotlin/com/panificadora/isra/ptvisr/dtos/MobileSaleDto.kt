package com.panificadora.isra.ptvisr.dtos

data class MobileSaleDto(
    val items: List<MobileSaleItemDto>
)

data class MobileSaleItemDto(
    val productId: Long,
    val quantity: Double,
    val price: Double
)