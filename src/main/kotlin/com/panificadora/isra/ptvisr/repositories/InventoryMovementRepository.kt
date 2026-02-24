package com.panificadora.isra.ptvisr.repositories

import com.panificadora.isra.ptvisr.models.InventoryMovement
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface InventoryMovementRepository : JpaRepository<InventoryMovement, Long>
