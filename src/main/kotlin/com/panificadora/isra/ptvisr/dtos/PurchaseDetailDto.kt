package com.panificadora.isra.ptvisr.dtos

import java.math.BigDecimal

data class PurchaseDetailDto(
    var productId: Long = 0,
    var quantity: BigDecimal = BigDecimal.ZERO,
    var price: BigDecimal = BigDecimal.ZERO
)
