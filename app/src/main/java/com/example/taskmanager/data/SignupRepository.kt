package com.example.taskmanager.data

import com.example.taskmanager.data.LoginRequest
import com.example.taskmanager.data.LoginResponse
import com.example.taskmanager.data.ApiServiceInterface
import javax.inject.Inject

class SignupRepository @Inject constructor(
    private val apiService: ApiServiceInterface
) {
    suspend fun signup(loginRequest: LoginRequest): LoginResponse {
        return apiService.signup(loginRequest)
    }
}