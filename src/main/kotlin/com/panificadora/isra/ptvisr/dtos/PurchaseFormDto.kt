package com.panificadora.isra.ptvisr.dtos

import java.time.LocalDateTime

data class PurchaseFormDto(
    var supplierId: Long? = null,
    var purchaseDate: LocalDateTime = LocalDateTime.now(),
    var purchaseDetails: List<PurchaseDetailDto> = emptyList()
) {
    // Calculate total from details for convenience, though it will be recalculated in service
    val total: Double
        get() = purchaseDetails.sumOf { it.quantity * it.price }
}
