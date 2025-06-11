package com.example.taskmanager.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Profile(
    @SerialName("id")
    val id: Int,
    @SerialName("username")
    val username: String,
    @SerialName("email")
    val email: String,
    @SerialName("createdAt")
    val createdAt: String,
    @SerialName("updatedAt")
    val updatedAt: String
) {
    init {
        if (username.isNotBlank()) {
            require(username.length >= 6) { "Username must be at least 6 characters" }
        }
    }

    companion object {
        fun create(username: String? = null, password: String? = null): Profile {
            return Profile(
                id = 0, // This will be set by the backend
                username = username ?: "",
                email = "", // This will be preserved by the backend
                createdAt = "", // This will be set by the backend
                updatedAt = "" // This will be set by the backend
            )
        }
    }
}