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
    suspend fun addTask(taskAddRequest: TaskAddRequest): TaskListResponse{
        return apiService.addTask(taskAddRequest)
    }
}