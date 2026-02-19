package com.panificadora.isra.ptvisr.repositories

import com.panificadora.isra.ptvisr.models.Category
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CategoryRepository : JpaRepository<Category, Int>
