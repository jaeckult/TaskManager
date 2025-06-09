package com.example.taskmanager.data

import android.content.Context
import androidx.core.content.edit

class AuthPrefs(context: Context) {
    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_TOKEN = "token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_EMAIL = "email"
    }

    // Save authentication data after login or signup
    fun saveAuthData(
        token: String?,
        userId: String,
        username: String,
        email: String

    ) {
        prefs.edit {
            if (token != null) putString(KEY_TOKEN, token) else remove(KEY_TOKEN)
            putString(KEY_USER_ID, userId)
            putString(KEY_USERNAME, username)
            putString(KEY_EMAIL, email)
        }
    }

    // Check if user is authenticated (has a valid token)
    fun isAuthenticated(): Boolean {
        return prefs.getString(KEY_TOKEN, null) != null
    }




    // Get user ID
    fun getUserId(): String? {
        return prefs.getString(KEY_USER_ID, null)
    }

    // Get username
    fun getUsername(): String? {
        return prefs.getString(KEY_USERNAME, null)
    }
    fun getEmail(): String? {
        return prefs.getString(KEY_EMAIL, null)
    }


    // Get token
    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    // Clear all authentication data (e.g., on logout)
    fun clearAuthData() {
        prefs.edit { clear() }
    }
    fun setUsername(string: String) {
        prefs.edit {
            putString(KEY_USERNAME, string)
        }

    }

}