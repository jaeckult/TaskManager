package com.example.taskmanager.presentaion.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.DateRange
//import androidx.compose.material.icons.outlined.GridView
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
import com.example.taskmanager.application.TaskDashboardState
import com.example.taskmanager.application.TaskDashboardViewModel
import com.example.taskmanager.application.TaskListState
import com.example.taskmanager.data.TaskListResponse
import com.example.taskmanager.presentaion.Navigation.Routes
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import dagger.hilt.android.lifecycle.HiltViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    viewModel: TaskDashboardViewModel = hiltViewModel(),
    navController: NavController
) {
    val dashboardState = viewModel.taskDashboardState.value
    val taskListState = viewModel.taskListState.value
    var currentDate by remember { mutableStateOf(LocalDate.now()) }

    LaunchedEffect(Unit) {
        viewModel.getTaskDashboard()
        viewModel.getTaskList()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Routes.ADD_TASK) },
                containerColor = Color(0xFF00BFFF)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task", tint = Color.White)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Tasks", modifier = Modifier.align(Alignment.CenterHorizontally), fontSize = 24.sp, fontWeight = FontWeight.Bold)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    currentDate.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )

            }

            Spacer(modifier = Modifier.height(8.dp))

            if (dashboardState is TaskDashboardState.Success) {
                val stats = dashboardState.taskDashboard
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        StatBox("Task", stats.totalTasks, Color(0xFF9575CD))
                        StatBox("Completed", stats.completedTasksQuantity, Color(0xFF81C784))
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        StatBox("Expired", stats.expiredTasksQuantity, Color(0xFFB0BEC5))
                        StatBox("Cancelled", stats.cancelledTasksQuantity, Color(0xFFEF5350))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("TASKS", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("status", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ✅ Dynamic Task List
            when (taskListState) {
                is TaskListState.Success -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(taskListState.taskList) { task ->
                            TaskRow(
                                name = task.title,
                                dateRange = "${task.startDate.take(10)} - ${task.endDate.take(10)}",
                                status = task.status,
                                taskId = task.id.toString(),
                                onTaskClick = { taskId ->
                                    navController.navigate(Routes.TASK_DETAIL.replace("{taskId}", taskId))
                                }
                            )
                        }
                    }
                }
                is TaskListState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                is TaskListState.Error -> {
                    Text("Failed to load tasks", color = Color.Red)
                }
                else -> {}
            }
        }
    }
}

@Composable
fun TaskRow(
    name: String,
    dateRange: String,
    status: String,
    taskId: String,
    onTaskClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTaskClick(taskId) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(text = dateRange, fontSize = 12.sp, color = Color.Gray)
        }

        val (emoji, color, label) = when (status.uppercase()) {
            "COMPLETED" -> Triple("✅", Color(0xFF4CAF50), "COMPLETE")
            "EXPIRED" -> Triple("⏰", Color(0xFFF44336), "EXPIRED")
            "CANCELLED" -> Triple("❌", Color.Gray, "CANCELLED")
            "TODO" -> Triple("📝", Color(0xFFFFC107), "TODO")
            "IN_PROGRESS" -> Triple("🔄", Color(0xFF2196F3), "IN PROGRESS")
            else -> Triple("❓", Color.LightGray, status.uppercase())
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = emoji, fontSize = 18.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = label, color = color, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun StatBox(label: String, count: Int, color: Color) {
    Box(
        modifier = Modifier
            .size(130.dp)
            .background(color, shape = RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = label, fontSize = 16.sp, color = Color.Black)
            Text(text = "$count", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}

@Composable
fun StatCard(label: String, count: Int, color: Color) {
    Card(
        modifier = Modifier
            .size(100.dp, 80.dp)
            .background(color, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = label, color = Color.White, fontSize = 16.sp)
                Text(text = count.toString(), color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

