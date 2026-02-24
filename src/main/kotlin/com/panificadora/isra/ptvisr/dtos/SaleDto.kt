package com.panificadora.isra.ptvisr.dtos

data class SaleDto(
    val details: List<SaleDetailDto>
)

data class SaleDetailDto(
    val productId: Long,
    val quantity: Double,
    val unitPrice: Double
)
