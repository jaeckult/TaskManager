package com.example.taskmanager.application

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.data.AuthPrefs
import com.example.taskmanager.data.LoginRepository
import com.example.taskmanager.data.LoginRequest
import com.example.taskmanager.data.SignupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SignupState {
    object Idle : SignupState()
    object Loading : SignupState()
    data class Success(val signupRequest: LoginRequest) : SignupState()
    data class Error(val message: String) : SignupState()
}

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val signupRepository: SignupRepository,
    private val authPrefs: AuthPrefs
) : ViewModel() {

    var signupState = mutableStateOf<SignupState>(SignupState.Idle)
        private set

    var username = mutableStateOf("")
        private set

    var password = mutableStateOf("")
        private set

    var signupError = mutableStateOf(false)
        private set

    fun setUserName(value: String) {
        username.value = value.trim()
    }

    fun setPassword(value: String) {
        password.value = value
        signupError.value = false
    }

    fun signup() {
        Log.d("SIGNUP_VIEWMODEL", "signup() triggered with ${username.value} and ${password.value}")
        signupState.value = SignupState.Loading

        viewModelScope.launch {
            try {
                val user = signupRepository.signup(
                    LoginRequest(
                        username = username.value,
                        password = password.value
                    )
                )

                signupState.value = SignupState.Success(
                    signupRequest = LoginRequest(username = username.value, password = password.value)
                )
                signupError.value = false

                authPrefs.saveAuthData(
                    token = user.token,
                    userId = user.id.toString(),
                    username = user.username,
                    email = user.email
                )

                Log.d("SIGNUP", "Username: ${username.value}, Password: ${password.value}")

            } catch (e: Exception) {
                signupState.value = SignupState.Error("Signup failed because of ${e.message}")
                signupError.value = true
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
