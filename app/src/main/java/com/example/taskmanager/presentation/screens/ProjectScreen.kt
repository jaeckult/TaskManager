package com.example.taskmanager.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.taskmanager.application.ProjectState
import com.example.taskmanager.application.ProjectViewModel
import com.example.taskmanager.application.ShareRequestState
import com.example.taskmanager.data.Project
import com.example.taskmanager.data.ProjectShareRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectScreen(
    viewModel: ProjectViewModel = hiltViewModel(),
    onNavigateToTask: (Int) -> Unit,
    navController: NavHostController
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var showShareDialog by remember { mutableStateOf(false) }
    var selectedProject by remember { mutableStateOf<Project?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadProjects()
        viewModel.loadShareRequests()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Projects") },
                actions = {
                    IconButton(onClick = { showCreateDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Create Project")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (val state = viewModel.projectState.value) {
                is ProjectState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is ProjectState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(state.projects) { project ->
                            ProjectItem(
                                project = project,
                                onShareClick = {
                                    selectedProject = project
                                    showShareDialog = true
                                },
                                onDeleteClick = { viewModel.deleteProject(project.id) },
                                onTaskClick = { onNavigateToTask(project.id) }
                            )
                        }
                    }
                }
                is ProjectState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> Unit
            }
        }
    }

    if (showCreateDialog) {
        CreateProjectDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = {
                viewModel.createProject()
                showCreateDialog = false
            },
            title = viewModel.title.value,
            description = viewModel.description.value,
            onTitleChange = viewModel::setTitle,
            onDescriptionChange = viewModel::setDescription
        )
    }

    if (showShareDialog && selectedProject != null) {
        ShareProjectDialog(
            onDismiss = { showShareDialog = false },
            onConfirm = { targetUserId ->
                viewModel.shareProject(selectedProject!!.id, targetUserId)
                showShareDialog = false
            }
        )
    }

    // Show share requests
    when (val state = viewModel.shareRequestState.value) {
        is ShareRequestState.Success -> {
            if (state.requests.isNotEmpty()) {
                ShareRequestsDialog(
                    requests = state.requests,
                    onAccept = { requestId -> viewModel.handleShareRequest(requestId, true) },
                    onDecline = { requestId -> viewModel.handleShareRequest(requestId, false) }
                )
            }
        }
        else -> Unit
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectItem(
    project: Project,
    onShareClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onTaskClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        onClick = onTaskClick
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = project.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Row {
                    IconButton(onClick = onShareClick) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            }
            if (!project.description.isNullOrBlank()) {
                Text(
                    text = project.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            Text(
                text = "${project.tasks.size} tasks",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun CreateProjectDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    title: String,
    description: String,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Project") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = onTitleChange,
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ShareProjectDialog(
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var userId by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Share Project") },
        text = {
            OutlinedTextField(
                value = userId,
                onValueChange = { userId = it },
                label = { Text("User ID") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    userId.toIntOrNull()?.let { onConfirm(it) }
                }
            ) {
                Text("Share")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ShareRequestsDialog(
    requests: List<ProjectShareRequest>,
    onAccept: (Int) -> Unit,
    onDecline: (Int) -> Unit
) {
    AlertDialog(
        onDismissRequest = { },
        title = { Text("Share Requests") },
        text = {
            Column {
                requests.forEach { request ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "From: ${request.fromUser.username}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Project: ${request.project.title}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Row {
                            IconButton(onClick = { onAccept(request.id) }) {
                                Icon(Icons.Default.Check, contentDescription = "Accept")
                            }
                            IconButton(onClick = { onDecline(request.id) }) {
                                Icon(Icons.Default.Close, contentDescription = "Decline")
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { }) {
                Text("Close")
            }
        }
    )
} 