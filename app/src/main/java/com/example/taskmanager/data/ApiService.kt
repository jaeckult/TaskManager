package com.example.taskmanager.data

import retrofit2.http.*
import com.example.taskmanager.data.LoginRequest
import com.example.taskmanager.data.LoginResponse
import com.example.taskmanager.data.Profile

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
    
    @GET("api/users/profile")
    suspend fun getProfile(): Profile
    
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

    // Project endpoints
    @GET("api/projects")
    suspend fun getProjects(): List<Project>

    @GET("api/projects/{id}")
    suspend fun getProject(@Path("id") id: Int): Project

    @POST("api/projects")
    suspend fun createProject(@Body request: CreateProjectRequest): Project

    @PATCH("api/projects/{id}")
    suspend fun updateProject(
        @Path("id") id: Int,
        @Body request: UpdateProjectRequest
    ): Project

    @DELETE("api/projects/{id}")
    suspend fun deleteProject(@Path("id") id: Int)

    @POST("api/projects/{id}/share")
    suspend fun shareProject(
        @Path("id") projectId: Int,
        @Body request: ShareProjectRequest
    ): ProjectShareRequest

    @PATCH("api/projects/share/{requestId}")
    suspend fun handleShareRequest(
        @Path("requestId") requestId: Int,
        @Body request: HandleShareRequest
    ): ProjectShareRequest

    @GET("api/projects/share/requests")
    suspend fun getShareRequests(): List<ProjectShareRequest>
}