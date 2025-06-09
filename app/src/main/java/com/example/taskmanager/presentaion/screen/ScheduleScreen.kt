package com.example.taskmanager.presentaion.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.taskmanager.application.TaskDashboardViewModel
import com.example.taskmanager.application.TaskListState
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScheduleScreen(
    navController: NavController,
    viewModel: TaskDashboardViewModel = hiltViewModel()
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val taskListState = viewModel.taskListState.value

    LaunchedEffect(Unit) {
        viewModel.getTaskList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Month and Year Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                Text("<", fontSize = 20.sp)
            }
            Text(
                currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
            IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                Text(">", fontSize = 20.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Days of Week Header
        Row(modifier = Modifier.fillMaxWidth()) {
            DayOfWeek.values().forEach { dayOfWeek ->
                Text(
                    text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Calendar Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Add empty cells for days before the first day of the month
            val firstDayOfMonth = currentMonth.atDay(1)
            val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value
            items((1 until firstDayOfWeek).toList()) {
                Box(modifier = Modifier.aspectRatio(1f))
            }

            // Add days of the month
            items(currentMonth.lengthOfMonth()) { day ->
                val date = currentMonth.atDay(day + 1)
                val isSelected = date == selectedDate
                val isToday = date == LocalDate.now()
                
                // Check if there are tasks on this date
                val hasTasks = when (taskListState) {
                    is TaskListState.Success -> {
                        taskListState.taskList.any { task ->
                            val taskStartDate = LocalDate.parse(task.startDate.take(10))
                            val taskEndDate = LocalDate.parse(task.endDate.take(10))
                            date in taskStartDate..taskEndDate
                        }
                    }
                    else -> false
                }

                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(4.dp)
                        .background(
                            when {
                                isSelected -> Color(0xFF00BFFF)
                                isToday -> Color(0xFFE3F2FD)
                                else -> Color.Transparent
                            },
                            CircleShape
                        )
                        .clickable { selectedDate = date },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = (day + 1).toString(),
                            color = when {
                                isSelected -> Color.White
                                isToday -> Color(0xFF00BFFF)
                                else -> Color.Black
                            },
                            fontSize = 16.sp
                        )
                        if (hasTasks) {
                            Box(
                                modifier = Modifier
                                    .size(4.dp)
                                    .background(
                                        if (isSelected) Color.White else Color(0xFF00BFFF),
                                        CircleShape
                                    )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Selected Date Info and Tasks
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Selected Date",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = selectedDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")),
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Show tasks for selected date
                when (taskListState) {
                    is TaskListState.Success -> {
                        val tasksForDate = taskListState.taskList.filter { task ->
                            val taskStartDate = LocalDate.parse(task.startDate.take(10))
                            val taskEndDate = LocalDate.parse(task.endDate.take(10))
                            selectedDate in taskStartDate..taskEndDate
                        }
                        
                        if (tasksForDate.isNotEmpty()) {
                            Text(
                                text = "Tasks for this date:",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            tasksForDate.forEach { task ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    val statusColor = when (task.status.uppercase()) {
                                        "COMPLETED" -> Color(0xFF4CAF50)
                                        "EXPIRED" -> Color(0xFFF44336)
                                        "CANCELLED" -> Color.Gray
                                        "TODO" -> Color(0xFFFFC107)
                                        "IN_PROGRESS" -> Color(0xFF2196F3)
                                        else -> Color.LightGray
                                    }
                                    
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .background(statusColor, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = task.title,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = "No tasks scheduled for this date",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                    is TaskListState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                    is TaskListState.Error -> {
                        Text(
                            text = "Failed to load tasks",
                            color = Color.Red,
                            fontSize = 14.sp
                        )
                    }
                    else -> {}
                }
            }
        }
    }
} 