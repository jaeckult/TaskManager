package com.example.taskmanager.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.Contextual

@Serializable
data class Project(
    val id: Int,
    val title: String,
    val description: String? = null,
    val ownerId: Int,
    @Contextual
    val owner: ProjectOwner? = null,
    val tasks: List<ProjectTask> = emptyList(),
    @Contextual
    val sharedWith: List<ProjectSharedUser> = emptyList(),
    val createdAt: String? = null,
    val updatedAt: String? = null
)

@Serializable
data class ProjectOwner(
    val username: String,
    val id: Int? = null,
    val email: String? = null
)

@Serializable
data class ProjectTask(
    val id: Int,
    val userId: Int,
    val projectId: Int? = null,
    val title: String,
    val description: String,
    val startDate: String,
    val endDate: String,
    val createdAt: String,
    val updatedAt: String,
    val status: String,
    val date: String,
    @Contextual
    val user: ProjectTaskUser
)

@Serializable
data class ProjectTaskUser(
    val username: String,
    val id: Int? = null,
    val email: String? = null
)

@Serializable
data class ProjectSharedUser(
    val id: Int,
    val projectId: Int,
    val userId: Int,
    @Contextual
    val user: ProjectSharedUserInfo,
    val createdAt: String? = null
)

@Serializable
data class ProjectSharedUserInfo(
    val username: String,
    val id: Int? = null,
    val email: String? = null
)

@Serializable
data class ProjectShareRequest(
    val id: Int,
    val projectId: Int,
    val fromUserId: Int,
    val toUserId: Int,
    val status: String,
    @Contextual
    val project: ProjectShareRequestProject,
    @Contextual
    val fromUser: ProjectShareRequestUser,
    @Contextual
    val toUser: ProjectShareRequestUser? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

@Serializable
data class ProjectShareRequestProject(
    val id: Int,
    val title: String,
    val description: String? = null,
    val ownerId: Int,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

@Serializable
data class ProjectShareRequestUser(
    val id: Int,
    val username: String,
    val email: String? = null
)

@Serializable
data class CreateProjectRequest(
    val title: String,
    val description: String? = null
)

@Serializable
data class UpdateProjectRequest(
    val title: String,
    val description: String? = null
)

@Serializable
data class ShareProjectRequest(
    val targetUserId: Int
)

@Serializable
data class HandleShareRequest(
    val status: String // "ACCEPTED" or "DECLINED"
) 