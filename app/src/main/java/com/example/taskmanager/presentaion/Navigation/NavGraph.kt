//package com.example.taskmanager.presentaion.Navigation
//
//import androidx.compose.runtime.Composable
//import androidx.navigation.NavHostController
//import androidx.navigation.NavType
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.navArgument
//import com.example.taskmanager.presentaion.screen.*
//
//@Composable
//fun NavGraph(navController: NavHostController) {
//    NavHost(
//        navController = navController,
//        startDestination = Routes.LOGIN
//    ) {
//        composable(Routes.LOGIN) {
//            LoginScreen(navController = navController)
//        }
//        composable(Routes.SIGNUP) {
//            SignupScreen(navController = navController)
//        }
//        composable(Routes.HOME) {
//            HomeScreen(navController = navController)
//        }
//        composable(Routes.ADD_TASK) {
//            AddTaskScreen(navController = navController)
//        }
//        composable(Routes.PROFILE) {
//            ProfileScreen(navController = navController)
//        }
//        composable(
//            route = Routes.TASK_DETAIL,
//            arguments = listOf(
//                navArgument("taskId") {
//                    type = NavType.StringType
//                }
//            )
//        ) { backStackEntry ->
//            TaskDetailScreen(
//                navController = navController,
//                taskId = backStackEntry.arguments?.getString("taskId") ?: ""
//            )
//        }
//    }
//}
//
