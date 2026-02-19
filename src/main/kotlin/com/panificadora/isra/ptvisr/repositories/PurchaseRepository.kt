package com.panificadora.isra.ptvisr.repositories

import com.panificadora.isra.ptvisr.models.Purchase
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PurchaseRepository : JpaRepository<Purchase, Long>
