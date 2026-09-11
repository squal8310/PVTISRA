package com.panificadora.isra.ptvisr.dtos

data class AuthResponse(
    val token: String,
    val user: UserInfo
)

data class UserInfo(
    val id: Int,
    val username: String,
    val role: String
)