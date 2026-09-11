package com.panificadora.isra.ptvisr.repositories

import com.panificadora.isra.ptvisr.models.Sale
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface SaleRepository : JpaRepository<Sale, Long> {
    fun findBySaleDateBetween(startDate: LocalDateTime, endDate: LocalDateTime): List<Sale>
}
