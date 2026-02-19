package com.panificadora.isra.ptvisr.repositories

import com.panificadora.isra.ptvisr.models.Supplier
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SupplierRepository : JpaRepository<Supplier, Long>
