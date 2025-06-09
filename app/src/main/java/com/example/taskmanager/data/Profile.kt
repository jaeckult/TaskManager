package com.example.taskmanager.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Profile(
    @SerialName("username")
    val username: String? = null,
    @SerialName("password")
    val password: String? = null,
) {
    init {
        if (username != null) {
            require(username.isNotBlank()) { "Username cannot be empty" }
        }
        if (password != null) {
            require(password.length >= 6) { "Password must be at least 6 characters" }
        }
    }

    companion object {
        fun create(username: String? = null, password: String? = null): Profile {
            return Profile(
                username = username?.trim(),
                password = password
            )
        }
    }
}