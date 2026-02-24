package com.panificadora.isra.ptvisr.models

import jakarta.persistence.*

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

    @Column(nullable = false)
    var quantity: Double = 0.0,

    @Column(nullable = false)
    var price: Double = 0.0 // Price at the time of purchase
)
