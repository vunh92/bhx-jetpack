package com.vunh.jetpack.bhx.data.remote.model

data class LoginRequest(
    val username: String,
    val password: String,
    val expiresInMins: Int = 30
)

data class LoginResponse(
    val id: Int,
    val username: String,
    val firstName: String,
    val lastName: String,
    val email: String? = null,
    val phone: String? = null
)
