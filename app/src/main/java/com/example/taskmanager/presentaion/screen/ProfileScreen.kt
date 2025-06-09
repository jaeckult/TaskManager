package com.example.taskmanager.presentaion.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.taskmanager.application.ProfileEditState
import com.example.taskmanager.application.ProfileEditViewModel
import com.example.taskmanager.data.AuthPrefs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileEditViewModel = hiltViewModel(),
    authPrefs: AuthPrefs
) {
    val profileEditState = viewModel.profileEditState.value
    var showPasswordFields by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Current Profile Info
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Current Profile",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Username: ${authPrefs.getUsername()}")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Edit Profile Form
            OutlinedTextField(
                value = viewModel.username.value,
                onValueChange = { viewModel.setUsername(it) },
                label = { Text("New Username") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Password Change Section
            Button(
                onClick = { showPasswordFields = !showPasswordFields },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (showPasswordFields) "Hide Password Change" else "Change Password")
            }

            if (showPasswordFields) {
                OutlinedTextField(
                    value = viewModel.password.value,
                    onValueChange = { viewModel.setPassword(it) },
                    label = { Text("New Password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = viewModel.confirmPassword.value,
                    onValueChange = { viewModel.setConfirmPassword(it) },
                    label = { Text("Confirm Password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Update Button
            Button(
                onClick = { viewModel.updateProfile() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = viewModel.username.value.isNotBlank() || 
                         (showPasswordFields && viewModel.password.value.isNotBlank())
            ) {
                Text("Update Profile", fontSize = 16.sp)
            }

            // State Handling
            when (profileEditState) {
                is ProfileEditState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                is ProfileEditState.Error -> {
                    Text(
                        text = profileEditState.message,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                is ProfileEditState.Success -> {
                    LaunchedEffect(Unit) {
                        navController.navigateUp()
                    }
                }
                else -> {}
            }
        }
    }
}