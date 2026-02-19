package com.panificadora.isra.ptvisr.repositories

import com.panificadora.isra.ptvisr.models.UnitOfMeasure
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface
UnitOfMeasureRepository : JpaRepository<UnitOfMeasure, Int>
