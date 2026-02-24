package com.panificadora.isra.ptvisr.repositories

import com.panificadora.isra.ptvisr.models.Sale
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SaleRepository : JpaRepository<Sale, Long>
