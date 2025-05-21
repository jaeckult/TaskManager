package com.example.taskmanager.data

import com.example.taskmanager.data.LoginRequest
import com.example.taskmanager.data.LoginResponse
import com.example.taskmanager.data.ApiServiceInterface
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val apiService: ApiServiceInterface
) {
    suspend fun login(loginRequest: LoginRequest): LoginResponse {
        return apiService.login(loginRequest)
    }
}