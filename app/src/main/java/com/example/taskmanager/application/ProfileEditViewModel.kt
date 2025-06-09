package com.example.taskmanager.application

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.data.AuthPrefs
import com.example.taskmanager.data.Profile
import com.example.taskmanager.data.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ProfileEditState {
    object Idle : ProfileEditState()
    object Loading : ProfileEditState()
    data class Success(val profile: Profile) : ProfileEditState()
    data class Error(val message: String) : ProfileEditState()
}

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val authPrefs: AuthPrefs
) : ViewModel() {

    var profileEditState = mutableStateOf<ProfileEditState>(ProfileEditState.Idle)
        private set

    var profileEditError = mutableStateOf(false)
        private set

    var username = mutableStateOf("")
        private set
    var password = mutableStateOf("")
        private set
    var confirmPassword = mutableStateOf("")
        private set

    fun setUsername(value: String) {
        username.value = value.trim()
    }

    fun setPassword(value: String) {
        password.value = value
    }

    fun setConfirmPassword(value: String) {
        confirmPassword.value = value
    }

    fun updateProfile() {
        // Validate password if it's being changed
        if (password.value.isNotBlank()) {
            if (password.value != confirmPassword.value) {
                profileEditState.value = ProfileEditState.Error("Passwords do not match")
                return
            }
            if (password.value.length < 6) {
                profileEditState.value = ProfileEditState.Error("Password must be at least 6 characters")
                return
            }
        }

        // Validate username if it's being changed
        if (username.value.isNotBlank() && username.value.length < 3) {
            profileEditState.value = ProfileEditState.Error("Username must be at least 3 characters")
            return
        }

        // If no changes are being made
        if (username.value.isBlank() && password.value.isBlank()) {
            profileEditState.value = ProfileEditState.Error("No changes to update")
            return
        }

        Log.d("ProfileEditViewModel", "Updating profile")
        profileEditState.value = ProfileEditState.Loading

        viewModelScope.launch {
            try {
                profileEditError.value = false
                val userId = authPrefs.getUserId() ?: throw Exception("User not logged in")
                
                val newProfile = Profile.create(
                    username = if (username.value.isNotBlank()) username.value else null,
                    password = if (password.value.isNotBlank()) password.value else null
                )

                // Only proceed if we have at least one field to update
                if (newProfile.username == null && newProfile.password == null) {
                    profileEditState.value = ProfileEditState.Error("No changes to update")
                    return@launch
                }

                val response = profileRepository.setNewProfile(userId, newProfile)
                profileEditState.value = ProfileEditState.Success(response)
                
                // Update stored username if changed
                if (username.value.isNotBlank()) {
                    authPrefs.setUsername(username.value)
                }

            } catch (e: Exception) {
                Log.e("ProfileEditViewModel", "Error: ${e.message}")
                profileEditState.value = ProfileEditState.Error("Failed to update profile: ${e.message}")
                profileEditError.value = true
            }
        }
    }
}