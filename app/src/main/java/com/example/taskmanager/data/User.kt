package com.example.taskmanager.data

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val username: String,
    val email: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
) 