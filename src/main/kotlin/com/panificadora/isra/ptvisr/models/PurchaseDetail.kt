package com.panificadora.isra.ptvisr.models

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "purchase_details")
data class PurchaseDetail(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id", nullable = false)
    var purchase: Purchase? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    var product: Product? = null,

    @Column(nullable = false, precision = 10, scale = 3) // Scale 3 for quantity (e.g., 0.5 kg)
    var quantity: BigDecimal = BigDecimal.ZERO,

    @Column(nullable = false, precision = 10, scale = 2) // Scale 2 for price
    var price: BigDecimal = BigDecimal.ZERO // Price at the time of purchase
)
