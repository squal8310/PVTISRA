package com.panificadora.isra.ptvisr.models

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "inventory_movements")
data class InventoryMovement(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0, // Changed from Long to Int

    @Enumerated(EnumType.STRING)
    val movementType: MovementType,

    @Column(name = "sale_id")
    val sale: Long,

    @Column(name = "description")
    val description: String,

    val movementDate: LocalDateTime = LocalDateTime.now()
)

enum class MovementType {
    ENTRADA, // Inflow (e.g., purchase)
    SALIDA,  // Outflow (e.g., sale)
    AJUSTE   // Adjustment
}
