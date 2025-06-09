package com.example.taskmanager.data

import okhttp3.internal.concurrent.Task
import retrofit2.http.*

interface ApiServiceInterface {
    @Headers("Content-Type: application/json")
    @POST("api/login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse
    @POST("api/signup")
    suspend fun signup(@Body loginRequest: LoginRequest): LoginResponse
    @GET("api/tasks/stats")
    suspend fun getTaskDashboard(): TaskDashboardResponse
    @GET("api/tasks")
    suspend fun getTaskList(): List<TaskListResponse>
    @POST("api/tasks/")
    suspend fun addTask(@Body taskAddRequest: TaskAddRequest): TaskListResponse
    @PATCH("api/users/{id}")
    suspend fun setNewProfile(
        @Path("id") userId: String,
        @Body profile: Profile
    ): Profile
    
    // New endpoints for task detail operations
    @GET("api/tasks/{id}")
    suspend fun getTaskById(@Path("id") taskId: String): TaskListResponse
    
    @PATCH("api/tasks/{id}")
    suspend fun updateTask(
        @Path("id") taskId: String,
        @Body taskUpdateRequest: TaskUpdateRequest
    ): TaskListResponse
    
    @DELETE("api/tasks/{id}")
    suspend fun deleteTask(@Path("id") taskId: String)
}