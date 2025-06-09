package com.example.taskmanager.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind.INT

@Serializable
data class TaskDashboardResponse(
    val totalTasks: Int,
    val completedTasksQuantity: Int,
    val cancelledTasksQuantity : Int,
    val expiredTasksQuantity : Int,
    val TodoQuantity : Int
)

@Serializable
data class TaskListResponse(
    val id: Int,
    val userId: Int,
    val title: String,
    val description: String,
    val startDate: String,
    val endDate: String,
    val createdAt: String,
    val updatedAt: String,
    val status: String,
    val date: String
)

@Serializable
data class TaskAddRequest(
    val title: String,
    val description: String,
    val startDate: String,
    val endDate: String
)
