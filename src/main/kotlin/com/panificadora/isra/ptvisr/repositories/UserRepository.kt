package com.panificadora.isra.ptvisr.repositories

import com.panificadora.isra.ptvisr.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Int> {
    fun findByUserIdentifier(userIdentifier: String): User?
}
