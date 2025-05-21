package com.example.taskmanager.presentaion.screen



import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.taskmanager.R
import com.example.taskmanager.application.LoginState
import com.example.taskmanager.application.LoginViewModel

@Composable

fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),

    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit,
    navController: NavController
) {
    val orange = Color(0xFFFF7F50)
    val context = LocalContext.current
    var username by viewModel.username
    var password by   viewModel.password
    val loginState by viewModel.loginState
    val loginError by viewModel.loginError
    when (loginState) {
        is LoginState.Idle -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(Color.White)

                    .padding(horizontal = 24.dp)
                ,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))
                Image(
                    painter = painterResource(id = R.drawable.signup_illustration), // Replace with your asset
                    contentDescription = "Login Illustration",
                    modifier = Modifier
                        .height(280.dp)
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Back to sync?",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Access your account by logging in",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))


                OutlinedTextField(
                    value = username,
                    onValueChange = { viewModel.setUserName(it) },
                    label = { Text("Username") },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_key), // Replace with your email icon
                            contentDescription = "Username"
                        )
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(65.dp),
                    shape = RoundedCornerShape(12.dp),
                )

                Spacer(modifier = Modifier.height(24.dp))


                OutlinedTextField(
                    value = password,
                    onValueChange = { viewModel.setPassword(it) },
                    label = { Text("Password") },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_key), // Replace with your lock icon
                            contentDescription = "Password"
                        )
                    },
                    singleLine = true,
                    isError = loginError,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(65.dp),
                    shape = RoundedCornerShape(12.dp),

                    )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Forgot Password?",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.End)
                        .clickable {
                            navController.navigate("forgotpassword")
                        }
                )

                Spacer(modifier = Modifier.height(16.dp))


                Button(
                    onClick = {
//                        Log.d("LOGIN_SCREEN", "Login button clicked with: $username, $password")
                        viewModel.setUserName(username)
                        viewModel.setPassword(password)
                        viewModel.login()

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = password.length >= 6,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = orange)
                ) {
                    Text(
                        text = "Log in",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White

                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Divider(color = Color.LightGray, thickness = 1.dp)

                Spacer(modifier = Modifier.height(8.dp))

                Row {
                    Text(
                        text = "No account?",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Sign up",
                        color = orange,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable {
                            navController.navigate("signup")
                        }
                    )
                }
            }

        }
        is LoginState.Loading -> {
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center) // This will center the indicator
            )
        }
        is LoginState.Success -> {
            navController.navigate("home")

        }
        is LoginState.Error -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(Color.White)

                    .padding(horizontal = 24.dp)
                ,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))
                Image(
                    painter = painterResource(id = R.drawable.signup_illustration), // Replace with your asset
                    contentDescription = "Login Illustration",
                    modifier = Modifier
                        .height(280.dp)
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Back to sync?",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Access your account by logging in",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))


                OutlinedTextField(
                    value = username,
                    onValueChange = { viewModel.setUserName(it) },
                    label = { Text("Username") },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_key), // Replace with your email icon
                            contentDescription = "Username"
                        )
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(65.dp),
                    shape = RoundedCornerShape(12.dp),
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Password Field
                OutlinedTextField(
                    value = password,
                    onValueChange = { viewModel.setPassword(it) },
                    label = { Text("Password") },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_key), // Replace with your lock icon
                            contentDescription = "Password"
                        )
                    },
                    singleLine = true,
                    isError = loginError,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(65.dp),
                    shape = RoundedCornerShape(12.dp),

                    )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Forgot Password?",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.End)
                        .clickable {
                            navController.navigate("forgotpassword")
                        }
                )

                Spacer(modifier = Modifier.height(24.dp))


                Button(
                    onClick = {
                        viewModel.setUserName(username)
                        viewModel.setPassword(password)
                        viewModel.login()

                    },
                    enabled=password.length >=6,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = orange)
                ) {
                    Text(
                        text = "Log in",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White

                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Divider(color = Color.LightGray, thickness = 1.dp)

                Spacer(modifier = Modifier.height(8.dp))

                Row {
                    Text(
                        text = "No account?",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Sign up",
                        color = orange,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable {
                            navController.navigate("signup")
                        }
                    )
                }
            }



        }

    }


}