package com.panificadora.isra.ptvisr.models

import jakarta.persistence.*

@Entity
@Table(name = "suppliers")
data class Supplier(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String,
    @Column(name = "contact_name")
    val contactName: String?,
    val phone: String?,
    val email: String?
)
