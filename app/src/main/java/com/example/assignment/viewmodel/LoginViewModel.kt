package com.example.assignment.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.data.repository.UserRepository
import com.example.assignment.model.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val user: User) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

class LoginViewModel(private val context: Context) : ViewModel() {
    private val repository = UserRepository(context)
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun loginWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            delay(500)
            val result = repository.loginWithEmail(email.trim(), password.trim())
            if (result.isSuccess) {
                _uiState.value = LoginUiState.Success(result.getOrThrow())
            } else {
                _uiState.value = LoginUiState.Error(
                    result.exceptionOrNull()?.message ?: "Login failed"
                )
            }
        }
    }

    fun loginWithGoogle(user: User) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            delay(500)
            val result = repository.loginWithGoogle(user)
            if (result.isSuccess) {
                _uiState.value = LoginUiState.Success(result.getOrThrow())
            } else {
                _uiState.value = LoginUiState.Error(
                    result.exceptionOrNull()?.message ?: "Google login failed"
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }
}