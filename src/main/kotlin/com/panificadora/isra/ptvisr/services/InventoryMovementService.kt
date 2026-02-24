package com.panificadora.isra.ptvisr.services

import com.panificadora.isra.ptvisr.models.InventoryMovement
import com.panificadora.isra.ptvisr.repositories.InventoryMovementRepository
import org.springframework.stereotype.Service

@Service
class InventoryMovementService(
    private val inventoryMovementRepository: InventoryMovementRepository
) {
    fun saveMovement(movement: InventoryMovement): InventoryMovement {
        return inventoryMovementRepository.save(movement)
    }
}
