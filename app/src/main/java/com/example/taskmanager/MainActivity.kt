package com.example.taskmanager

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.taskmanager.application.LoginViewModel
import com.example.taskmanager.application.ProfileEditViewModel
import com.example.taskmanager.data.AuthPrefs
import com.example.taskmanager.presentaion.Navigation.Routes
import com.example.taskmanager.presentaion.screen.AddTaskScreen
import com.example.taskmanager.presentaion.screen.HomeScreen
import com.example.taskmanager.presentaion.screen.LoginScreen
import com.example.taskmanager.presentaion.screen.ProfileScreen
import com.example.taskmanager.presentaion.screen.SignupScreen
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
                    MyApp(
                    )
                }
            }
        }
    }
}
@Composable
fun MyApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val context = LocalContext.current
    val authPrefs = remember { AuthPrefs(context) }

    Scaffold(

    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.LOGIN,
            modifier = Modifier.padding(innerPadding)
        ){

            composable(Routes.LOGIN) {
                val context = LocalContext.current
                val viewModel: LoginViewModel = hiltViewModel()
                LoginScreen(

                    onLoginClick = {

                        navController.navigate(Routes.HOME) },
                    onSignUpClick = {
                        Toast.makeText(context, "Employee Clicked", Toast.LENGTH_SHORT).show()
                    },

                    navController = navController
                )
            }

            composable(Routes.HOME) {
                HomeScreen(
                    navController = navController
                )
            }
            composable(Routes.SIGNUP) {
                SignupScreen(
                    onLoginClick = {
                        navController.navigate(Routes.LOGIN) },
                    onSignUpClick = {
                        Toast.makeText(context, "Employee Clicked", Toast.LENGTH_SHORT).show()
                    },
                    navController = navController
                )
            }
            composable (Routes.ADD_TASK) {
                AddTaskScreen(
                    navController = navController

                )
            }
            composable(Routes.PROFILE) {
                ProfileScreen(
                    navController = navController,
                    authPrefs = authPrefs
                )
            }
        }
    }
}

