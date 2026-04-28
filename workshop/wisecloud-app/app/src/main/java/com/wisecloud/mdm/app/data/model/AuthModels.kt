package com.wisecloud.mdm.app.data.model

/** POST /api/auth/register request body */
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

/** POST /api/auth/login request body */
data class LoginRequest(
    val username: String,
    val password: String
)

/** POST /api/auth/login response data */
data class LoginResponse(
    val token: String,
    val expiresIn: Long
)
