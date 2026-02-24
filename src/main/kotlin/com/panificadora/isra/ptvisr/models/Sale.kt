package com.panificadora.isra.ptvisr.models

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "sales")
data class Sale(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val saleDate: LocalDateTime = LocalDateTime.now(),

    val totalAmount: Double,

    val ivaAmount: Double = 0.0, // Default to 0.0 as it's transient

    val subtotalAmount: Double = 0.0, // Default to 0.0 as it's transient
    val customerId: Long? = null,
    val userId: Long? = null,

    @OneToMany(mappedBy = "sale", cascade = [CascadeType.ALL], orphanRemoval = true)
    val details: MutableList<SaleDetail> = mutableListOf()
)
