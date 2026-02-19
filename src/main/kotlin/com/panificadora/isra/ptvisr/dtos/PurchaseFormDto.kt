package com.panificadora.isra.ptvisr.dtos

import java.time.LocalDateTime
import java.math.BigDecimal

data class PurchaseFormDto(
    var supplierId: Long? = null,
    var purchaseDate: LocalDateTime = LocalDateTime.now(),
    var purchaseDetails: List<PurchaseDetailDto> = emptyList()
) {
    // Calculate total from details for convenience, though it will be recalculated in service
    val total: BigDecimal
        get() = purchaseDetails.sumOf { it.quantity.multiply(it.price) }
}
