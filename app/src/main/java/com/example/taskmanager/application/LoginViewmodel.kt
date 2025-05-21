package com.example.taskmanager.application

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.data.AuthPrefs
import com.example.taskmanager.data.LoginRepository
import com.example.taskmanager.data.LoginRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.String
sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val loginRequest: LoginRequest) : LoginState()
    data class Error(val message: String) : LoginState()

}
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val authPrefs: AuthPrefs
): ViewModel(){
    var loginState = mutableStateOf<LoginState>(LoginState.Idle)
        private set
    var username = mutableStateOf("")
        private set
    var password = mutableStateOf("")
        private set
    var loginError = mutableStateOf(false)
        private set
    fun setUserName(value:String) {
        val trimmed = value.trim()
        username.value = trimmed
    }
    fun setPassword(value:String){
        password.value = value
        loginError.value = false
    }
    fun login() {
        Log.d("LOGIN_VIEWMODEL", "login() triggered with $username and $password")
        loginState.value = LoginState.Loading
        viewModelScope.launch {


            try {



                val user = loginRepository.login(
                    LoginRequest(
                        username = username.value.toString(),
                        password = password.value.toString()
                    )
                )
                loginState.value = LoginState.Success(loginRequest = LoginRequest(username = username.value, password = password.value))
                loginError.value = false

                authPrefs.saveAuthData(
                    token = user.token,
                    userId = user.id.toString(),
                    username = user.username,
                    email = user.email
                )
                Log.d("LOGIN", "Username: $username, Password: $password")

            } catch (e: Exception) {
                loginState.value = LoginState.Error("Login failed because of ${e.message}")
                loginError.value = true

            }
        }
    }

    private fun saveAuthPreferences() {
        authPrefs.saveAuthData(
            token = authPrefs.getToken(), // Preserve existing token
            username = authPrefs.getUsername() ?: "",
            email = authPrefs.getEmail() ?: "",
            userId = authPrefs.getUserId() ?: ""
        )
    }




}