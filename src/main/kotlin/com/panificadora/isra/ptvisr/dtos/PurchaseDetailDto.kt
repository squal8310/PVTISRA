package com.panificadora.isra.ptvisr.dtos

data class PurchaseDetailDto(
    var productId: Long = 0,
    var quantity: Double = 0.0,
    var price: Double = 0.0
)
