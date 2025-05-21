package com.example.taskmanager.data

import com.example.taskmanager.data.LoginResponse
import com.example.taskmanager.data.LoginRequest

import retrofit2.http.*

interface ApiServiceInterface {
    @Headers("Content-Type: application/json")
    @POST("api/login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse

    @POST("api/signup")
    suspend fun signup(@Body loginRequest: LoginRequest): LoginResponse
}