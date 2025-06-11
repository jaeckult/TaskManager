package com.example.taskmanager.data

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val apiService: ApiServiceInterface
) {
    /**
     * Updates the user's profile with new username and/or password
     * @param userId The ID of the user whose profile is being updated
     * @param profile The new profile information
     * @return The updated profile
     * @throws Exception if the API call fails or if the user is not found
     */
    suspend fun setNewProfile(userId: String, profile: Profile): Profile {
        return try {
            apiService.setNewProfile(userId, profile)
        } catch (e: Exception) {
            throw Exception("Failed to update profile: ${e.message}")
        }
    }

    /**
     * Gets the current user's profile
     * @return The user's profile
     * @throws Exception if the API call fails
     */
    suspend fun getProfile(): Profile {
        return try {
            apiService.getProfile()
        } catch (e: Exception) {
            throw Exception("Failed to get profile: ${e.message}")
        }
    }
}