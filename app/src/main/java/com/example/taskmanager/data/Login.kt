package com.example.taskmanager.data


import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind.INT

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)
@Serializable
data class LoginResponse(
    val id: Int,
    val username: String,
    val email: String,
    val token: String? = null
)