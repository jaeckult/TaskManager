package com.example.taskmanager.application

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.data.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ProjectState {
    object Idle : ProjectState()
    object Loading : ProjectState()
    data class Success(val projects: List<Project>) : ProjectState()
    data class Error(val message: String) : ProjectState()
}

sealed class ShareRequestState {
    object Idle : ShareRequestState()
    object Loading : ShareRequestState()
    data class Success(val requests: List<ProjectShareRequest>) : ShareRequestState()
    data class Error(val message: String) : ShareRequestState()
}

@HiltViewModel
class ProjectViewModel @Inject constructor(
    private val projectRepository: ProjectRepository
) : ViewModel() {

    var projectState = mutableStateOf<ProjectState>(ProjectState.Idle)
        private set

    var shareRequestState = mutableStateOf<ShareRequestState>(ShareRequestState.Idle)
        private set

    var title = mutableStateOf("")
        private set

    var description = mutableStateOf("")
        private set

    var shareError = mutableStateOf(false)
        private set

    fun setTitle(value: String) {
        title.value = value.trim()
    }

    fun setDescription(value: String) {
        description.value = value.trim()
    }

    fun loadProjects() {
        projectState.value = ProjectState.Loading
        viewModelScope.launch {
            try {
                val projects = projectRepository.getProjects()
                projectState.value = ProjectState.Success(projects)
            } catch (e: Exception) {
                projectState.value = ProjectState.Error("Failed to load projects: ${e.message}")
            }
        }
    }

    fun createProject() {
        if (title.value.isBlank()) {
            projectState.value = ProjectState.Error("Title is required")
            return
        }

        projectState.value = ProjectState.Loading
        viewModelScope.launch {
            try {
                val request = CreateProjectRequest(
                    title = title.value,
                    description = description.value.takeIf { it.isNotBlank() }
                )
                projectRepository.createProject(request)
                loadProjects()
                title.value = ""
                description.value = ""
            } catch (e: Exception) {
                projectState.value = ProjectState.Error("Failed to create project: ${e.message}")
            }
        }
    }

    fun updateProject(id: Int) {
        if (title.value.isBlank()) {
            projectState.value = ProjectState.Error("Title is required")
            return
        }

        projectState.value = ProjectState.Loading
        viewModelScope.launch {
            try {
                val request = UpdateProjectRequest(
                    title = title.value,
                    description = description.value.takeIf { it.isNotBlank() }
                )
                projectRepository.updateProject(id, request)
                loadProjects()
                title.value = ""
                description.value = ""
            } catch (e: Exception) {
                projectState.value = ProjectState.Error("Failed to update project: ${e.message}")
            }
        }
    }

    fun deleteProject(id: Int) {
        projectState.value = ProjectState.Loading
        viewModelScope.launch {
            try {
                projectRepository.deleteProject(id)
                loadProjects()
            } catch (e: Exception) {
                projectState.value = ProjectState.Error("Failed to delete project: ${e.message}")
            }
        }
    }

    fun shareProject(projectId: Int, targetUserId: Int) {
        shareRequestState.value = ShareRequestState.Loading
        viewModelScope.launch {
            try {
                val request = ShareProjectRequest(targetUserId = targetUserId)
                projectRepository.shareProject(projectId, request)
                loadShareRequests()
            } catch (e: Exception) {
                shareRequestState.value = ShareRequestState.Error("Failed to share project: ${e.message}")
            }
        }
    }

    fun handleShareRequest(requestId: Int, accept: Boolean) {
        shareRequestState.value = ShareRequestState.Loading
        viewModelScope.launch {
            try {
                val request = HandleShareRequest(status = if (accept) "ACCEPTED" else "DECLINED")
                projectRepository.handleShareRequest(requestId, request)
                loadShareRequests()
                loadProjects() // Reload projects in case of acceptance
            } catch (e: Exception) {
                shareRequestState.value = ShareRequestState.Error("Failed to handle request: ${e.message}")
            }
        }
    }

    fun loadShareRequests() {
        shareRequestState.value = ShareRequestState.Loading
        viewModelScope.launch {
            try {
                val requests = projectRepository.getShareRequests()
                shareRequestState.value = ShareRequestState.Success(requests)
            } catch (e: Exception) {
                shareRequestState.value = ShareRequestState.Error("Failed to load share requests: ${e.message}")
            }
        }
    }
} 