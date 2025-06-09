package com.example.taskmanager.application

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.data.AuthPrefs
import com.example.taskmanager.data.HomeRepository
import com.example.taskmanager.data.LoginRequest
import com.example.taskmanager.data.TaskAddRequest
import com.example.taskmanager.data.TaskDashboardResponse
import com.example.taskmanager.data.TaskListResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.String
sealed class TaskDashboardState {
    object Idle : TaskDashboardState()
    object Loading : TaskDashboardState()
    data class Success(val taskDashboard: TaskDashboardResponse) : TaskDashboardState()
    data class Error(val message: String) : TaskDashboardState()

}
sealed class TaskListState{
    object Idle : TaskListState()
    object Loading : TaskListState()
    data class Success(val taskList: List<TaskListResponse>) : TaskListState()
    data class Error(val message : String) : TaskListState()
}

sealed class TaskAddState{
    object Idle : TaskAddState()
    object Loading : TaskAddState()
    data class Success(val taskListResponse: TaskListResponse ) : TaskAddState()
    data class Error(val message : String) : TaskAddState()
}
@HiltViewModel
class TaskDashboardViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val authPrefs: AuthPrefs
) : ViewModel() {

    var taskDashboardState = mutableStateOf<TaskDashboardState>(TaskDashboardState.Idle)
        private set

    var taskDashboardError = mutableStateOf(false)
        private set

    var taskListState = mutableStateOf<TaskListState>(TaskListState.Idle)
        private set
    var taskListError = mutableStateOf(false)
        private set

    var taskAddState = mutableStateOf<TaskAddState>(TaskAddState.Idle)
        private set
    var taskAddError = mutableStateOf(false)
        private set

    var title = mutableStateOf("")
        private set
    var description = mutableStateOf("")
        private set
    var startDate = mutableStateOf("")
        private set
    var endDate = mutableStateOf("")
        private set


    fun setTitle(value:String) {
        val trimmed = value.trim()
        title.value = trimmed
    }
    fun setDescription(value:String){
        val trimmed = value.trim()
        description.value = value
    }
    fun setStartDate(value: String) {
        if (value.isNotBlank()) {
            startDate.value = value
        }
    }
    fun setEndDate(value: String) {
        if (value.isNotBlank()) {
            endDate.value = value
        }
    }




    fun getTaskDashboard() {
        Log.d("TaskDashboardViewModel", "getTaskDashboard() triggered")
        taskDashboardState.value = TaskDashboardState.Loading

        viewModelScope.launch {
            try {
                taskDashboardError.value = false

                val response = homeRepository.getTaskDashboard()
                taskDashboardState.value = TaskDashboardState.Success(response)

            } catch (e: Exception) {
                Log.e("TaskDashboardViewModel", "Error: ${e.message}")
                taskDashboardState.value = TaskDashboardState.Error("getTaskDashboard failed because of ${e.message}")
                taskDashboardError.value = true
            }
        }
    }
    fun getTaskList() {
        Log.d("TaskDashboardViewModel", "getTaskList() triggered")

        taskListState.value = TaskListState.Loading
        viewModelScope.launch {
            try{
                taskListError.value = false

                val response = homeRepository.getTaskList()
                taskListState.value = TaskListState.Success(response)
            } catch (e: Exception){
                Log.e("TaskList", "Error: ${e.message}")
                taskListState.value = TaskListState.Error("getTaskList failed because of ${e.message}")
                taskListError.value = true

            }
        }
    }
    fun addTask() {
        if (title.value.isBlank() || description.value.isBlank() || 
            startDate.value.isBlank() || endDate.value.isBlank()) {
            taskAddState.value = TaskAddState.Error("All fields are required")
            return
        }

        Log.d("task creation started", "running")
        taskAddState.value = TaskAddState.Loading
        viewModelScope.launch {
            try {
                taskAddError.value = false

                val newTask = homeRepository.addTask(
                    TaskAddRequest(
                        title = title.value,
                        description = description.value,
                        startDate = startDate.value,
                        endDate = endDate.value,
                    )
                )
                taskAddError.value = false
                taskAddState.value = TaskAddState.Success(taskListResponse = newTask)
                
                // Refresh the task list after adding
                getTaskList()
                getTaskDashboard()

            } catch (e: Exception) {
                taskAddState.value = TaskAddState.Error("Failed to add task: ${e.message}")
                taskAddError.value = true
            }
        }
    }
}
