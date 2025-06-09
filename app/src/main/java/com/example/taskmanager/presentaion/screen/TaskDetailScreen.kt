package com.example.taskmanager.presentaion.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.taskmanager.application.TaskDashboardViewModel
import com.example.taskmanager.application.TaskDetailState
import com.example.taskmanager.application.TaskUpdateState
import com.example.taskmanager.application.TaskDeleteState
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    viewModel: TaskDashboardViewModel = hiltViewModel(),
    navController: NavController,
    taskId: String
) {
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    val startDatePickerState = rememberDatePickerState()
    val endDatePickerState = rememberDatePickerState()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("TODO") }

    // Load task details when the screen is first displayed
    LaunchedEffect(taskId) {
        viewModel.getTaskById(taskId)
    }

    // Observe task detail state
    val taskDetailState = viewModel.taskDetailState.value
    val taskUpdateState = viewModel.taskUpdateState.value
    val taskDeleteState = viewModel.taskDeleteState.value

    // Update local state when task details are loaded
    LaunchedEffect(taskDetailState) {
        if (taskDetailState is TaskDetailState.Success) {
            title = taskDetailState.task.title
            description = taskDetailState.task.description
            startDate = taskDetailState.task.startDate
            endDate = taskDetailState.task.endDate
            status = taskDetailState.task.status
        }
    }

    // Handle navigation after successful update or delete
    LaunchedEffect(taskUpdateState, taskDeleteState) {
        when {
            taskUpdateState is TaskUpdateState.Success -> {
                navController.navigateUp()
            }
            taskDeleteState is TaskDeleteState.Success -> {
                navController.navigateUp()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Task Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        when (taskDetailState) {
            is TaskDetailState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is TaskDetailState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = taskDetailState.message,
                        color = Color.Red
                    )
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )

                    // Start Date
                    OutlinedTextField(
                        value = startDate,
                        onValueChange = { },
                        label = { Text("Start Date") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showStartDatePicker = true }) {
                                Icon(Icons.Default.DateRange, contentDescription = "Select Start Date")
                            }
                        }
                    )

                    // End Date
                    OutlinedTextField(
                        value = endDate,
                        onValueChange = { },
                        label = { Text("End Date") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showEndDatePicker = true }) {
                                Icon(Icons.Default.DateRange, contentDescription = "Select End Date")
                            }
                        }
                    )

                    // Status Section
                    Text("Status", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatusButton(
                            text = "TODO",
                            isSelected = status == "TODO",
                            onClick = { status = "TODO" },
                            enabled = status != "COMPLETED" && status != "EXPIRED" && status != "CANCELLED"
                        )
                        StatusButton(
                            text = "IN PROGRESS",
                            isSelected = status == "IN_PROGRESS",
                            onClick = { status = "IN_PROGRESS" },
                            enabled = status != "COMPLETED" && status != "EXPIRED" && status != "CANCELLED"
                        )
                        StatusButton(
                            text = "COMPLETED",
                            isSelected = status == "COMPLETED",
                            onClick = { status = "COMPLETED" },
                            enabled = status != "EXPIRED" && status != "CANCELLED"
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatusButton(
                            text = "EXPIRED",
                            isSelected = status == "EXPIRED",
                            onClick = { status = "EXPIRED" },
                            enabled = status != "COMPLETED" && status != "CANCELLED"
                        )
                        StatusButton(
                            text = "CANCELLED",
                            isSelected = status == "CANCELLED",
                            onClick = { status = "CANCELLED" },
                            enabled = status != "COMPLETED" && status != "EXPIRED"
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Update Button
                    Button(
                        onClick = {
                            viewModel.updateTask(
                                taskId = taskId,
                                title = title,
                                description = description,
                                startDate = startDate,
                                endDate = endDate,
                                status = status
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = title.isNotBlank() && description.isNotBlank() &&
                                startDate.isNotBlank() && endDate.isNotBlank()
                    ) {
                        Text("Update Task", fontSize = 16.sp)
                    }

                    // Clear Button for completed/expired/cancelled tasks
                    if (status in listOf("COMPLETED", "EXPIRED", "CANCELLED")) {
                        OutlinedButton(
                            onClick = {
                                viewModel.deleteTask(taskId)
                                navController.popBackStack()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.Red
                            )
                        ) {
                            Text("Clear Task", fontSize = 16.sp)
                        }
                    }

                    // Show loading indicator during update/delete
                    if (taskUpdateState is TaskUpdateState.Loading || 
                        taskDeleteState is TaskDeleteState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }

                    // Show error messages
                    if (taskUpdateState is TaskUpdateState.Error) {
                        Text(
                            text = taskUpdateState.message,
                            color = Color.Red,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                    if (taskDeleteState is TaskDeleteState.Error) {
                        Text(
                            text = taskDeleteState.message,
                            color = Color.Red,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }

        if (showStartDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showStartDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            startDatePickerState.selectedDateMillis?.let { millis ->
                                val date = java.time.Instant
                                    .ofEpochMilli(millis)
                                    .atZone(java.time.ZoneId.systemDefault())
                                    .toLocalDate()
                                startDate = date.format(DateTimeFormatter.ISO_DATE)
                            }
                            showStartDatePicker = false
                        }
                    ) {
                        Text("OK")
                    }
                }
            ) {
                DatePicker(
                    state = startDatePickerState,
                    title = { Text("Select Start Date") },
                    headline = { Text("Start Date") },
                    showModeToggle = false
                )
            }
        }

        if (showEndDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showEndDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            endDatePickerState.selectedDateMillis?.let { millis ->
                                val date = java.time.Instant
                                    .ofEpochMilli(millis)
                                    .atZone(java.time.ZoneId.systemDefault())
                                    .toLocalDate()
                                endDate = date.format(DateTimeFormatter.ISO_DATE)
                            }
                            showEndDatePicker = false
                        }
                    ) {
                        Text("OK")
                    }
                }
            ) {
                DatePicker(
                    state = endDatePickerState,
                    title = { Text("Select End Date") },
                    headline = { Text("End Date") },
                    showModeToggle = false
                )
            }
        }
    }
}

@Composable
fun StatusButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFF00BFFF) else Color.LightGray
        )
    ) {
        Text(text, fontSize = 12.sp)
    }
} 