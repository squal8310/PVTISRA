package com.panificadora.isra.ptvisr.repositories

import com.panificadora.isra.ptvisr.models.SaleDetail
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SaleDetailRepository : JpaRepository<SaleDetail, Long>
