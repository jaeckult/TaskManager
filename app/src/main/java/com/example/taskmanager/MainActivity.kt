package com.example.taskmanager

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.taskmanager.application.LoginViewModel
import com.example.taskmanager.application.ProfileEditViewModel
import com.example.taskmanager.data.AuthPrefs
import com.example.taskmanager.presentaion.Navigation.Routes
import com.example.taskmanager.presentaion.screen.*
import com.example.taskmanager.presentaion.ui.theme.TaskmanagerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TaskmanagerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MyApp()
                }
            }
        }
    }
}

@Composable
fun BottomNavBar(navController: NavController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentRoute == Routes.HOME,
            onClick = { navController.navigate(Routes.HOME) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.DateRange, contentDescription = "Schedule") },
            label = { Text("Schedule") },
            selected = currentRoute == Routes.SCHEDULE,
            onClick = { navController.navigate(Routes.SCHEDULE) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = currentRoute == Routes.PROFILE,
            onClick = { navController.navigate(Routes.PROFILE) }
        )
    }
}

@Composable
fun MyApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val context = LocalContext.current
    val authPrefs = remember { AuthPrefs(context) }

    // Define main screens that should show bottom navigation
    val mainScreens = listOf(Routes.HOME, Routes.SCHEDULE, Routes.PROFILE)

    Scaffold(
        bottomBar = {
            if (currentRoute in mainScreens) {
                BottomNavBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.LOGIN,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.LOGIN) {
                val context = LocalContext.current
                val viewModel: LoginViewModel = hiltViewModel()
                LoginScreen(
                    onLoginClick = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    },
                    onSignUpClick = {
                        Toast.makeText(context, "Employee Clicked", Toast.LENGTH_SHORT).show()
                    },
                    navController = navController
                )
            }

            composable(Routes.HOME) {
                HomeScreen(navController = navController)
            }

            composable(Routes.SCHEDULE) {
                ScheduleScreen(navController = navController)
            }

            composable(Routes.SIGNUP) {
                SignupScreen(
                    onLoginClick = {
                        navController.navigate(Routes.LOGIN)
                    },
                    onSignUpClick = {
                        Toast.makeText(context, "Employee Clicked", Toast.LENGTH_SHORT).show()
                    },
                    navController = navController
                )
            }

            composable(Routes.ADD_TASK) {
                AddTaskScreen(navController = navController)
            }

            composable(Routes.PROFILE) {
                ProfileScreen(
                    navController = navController,
                    authPrefs = authPrefs
                )
            }

            composable(
                route = Routes.TASK_DETAIL,
                arguments = listOf(
                    navArgument("taskId") {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                TaskDetailScreen(
                    navController = navController,
                    taskId = backStackEntry.arguments?.getString("taskId") ?: ""
                )
            }
        }
    }
}

