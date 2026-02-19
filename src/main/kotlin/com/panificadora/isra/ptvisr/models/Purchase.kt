package com.panificadora.isra.ptvisr.models

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "purchases")
data class Purchase(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    var supplier: Supplier? = null,

    @Column(nullable = false)
    var purchaseDate: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false, precision = 10, scale = 2)
    var total: BigDecimal = BigDecimal.ZERO,

    @OneToMany(mappedBy = "purchase", cascade = [CascadeType.ALL], orphanRemoval = true)
    val details: MutableList<PurchaseDetail> = mutableListOf()
)
