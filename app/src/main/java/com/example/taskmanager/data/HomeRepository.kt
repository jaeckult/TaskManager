package com.example.taskmanager.data

import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val apiService: ApiServiceInterface
) {
    suspend fun getTaskDashboard(): TaskDashboardResponse {
        return apiService.getTaskDashboard()
    }

    suspend fun getTaskList(): List<TaskListResponse> {
        return apiService.getTaskList()
    }
    
    suspend fun addTask(taskAddRequest: TaskAddRequest): TaskListResponse {
        return apiService.addTask(taskAddRequest)
    }
    
    suspend fun getTaskById(taskId: String): TaskListResponse {
        return apiService.getTaskById(taskId)
    }
    
    suspend fun updateTask(taskId: String, taskUpdateRequest: TaskUpdateRequest): TaskListResponse {
        return apiService.updateTask(taskId, taskUpdateRequest)
    }
    
    suspend fun deleteTask(taskId: String) {
        apiService.deleteTask(taskId)
    }
}