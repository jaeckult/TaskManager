package com.example.taskmanager.data

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectRepository @Inject constructor(
    private val apiService: ApiServiceInterface
) {
    suspend fun getProjects(): List<Project> {
        return apiService.getProjects()
    }

    suspend fun getProject(id: Int): Project {
        return apiService.getProject(id)
    }

    suspend fun createProject(request: CreateProjectRequest): Project {
        return apiService.createProject(request)
    }

    suspend fun updateProject(id: Int, request: UpdateProjectRequest): Project {
        return apiService.updateProject(id, request)
    }

    suspend fun deleteProject(id: Int) {
        apiService.deleteProject(id)
    }

    suspend fun shareProject(projectId: Int, request: ShareProjectRequest): ProjectShareRequest {
        return apiService.shareProject(projectId, request)
    }

    suspend fun handleShareRequest(requestId: Int, request: HandleShareRequest): ProjectShareRequest {
        return apiService.handleShareRequest(requestId, request)
    }

    suspend fun getShareRequests(): List<ProjectShareRequest> {
        return apiService.getShareRequests()
    }
} 