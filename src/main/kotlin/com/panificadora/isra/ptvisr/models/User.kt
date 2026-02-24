package com.panificadora.isra.ptvisr.models

import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
@Table(name = "users")
class User : UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0

    @Column(unique = true, nullable = false)
    var userIdentifier: String = "" // Renamed from 'username' to avoid clash

    @Column(nullable = false)
    var passwordHash: String = "" // Renamed from 'password_hash' for consistency

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: Role = Role.ADMIN

    // Default constructor for JPA
    constructor()

    // Secondary constructor for convenience
    constructor(id: Int = 0, userIdentifier: String, passwordHash: String, role: Role) : this() {
        this.id = id
        this.userIdentifier = userIdentifier
        this.passwordHash = passwordHash
        this.role = role
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf(SimpleGrantedAuthority("ROLE_${role.name}"))
    }

    // Explicitly implement UserDetails methods using the renamed properties
    override fun getPassword(): String = passwordHash
    override fun getUsername(): String = userIdentifier // Use the renamed property

    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true
}

enum class Role {
    ADMIN,
    CASHIER
}
