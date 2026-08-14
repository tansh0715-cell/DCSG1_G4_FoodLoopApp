package com.example.assignment.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.data.repository.UserRepository
import com.example.assignment.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class RegisterSaverUiState {
    object Idle : RegisterSaverUiState()
    object Loading : RegisterSaverUiState()
    object Success : RegisterSaverUiState()
    data class Error(val message: String) : RegisterSaverUiState()
}

class RegisterSaverViewModel(private val context: Context) : ViewModel() {
    private val repository = UserRepository(context)
    private val _uiState = MutableStateFlow<RegisterSaverUiState>(RegisterSaverUiState.Idle)
    val uiState: StateFlow<RegisterSaverUiState> = _uiState.asStateFlow()

    fun register(name: String, email: String, phone: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            val trimmedName = name.trim()
            val trimmedEmail = email.trim()
            val trimmedPhone = phone.trim()
            val trimmedPassword = password.trim()
            val trimmedConfirm = confirmPassword.trim()

            if (trimmedName.isEmpty() || trimmedEmail.isEmpty() || trimmedPhone.isEmpty() || trimmedPassword.isEmpty() || trimmedConfirm.isEmpty()) {
                _uiState.value = RegisterSaverUiState.Error("Please fill in all fields")
                return@launch
            }
            if (trimmedPassword != trimmedConfirm) {
                _uiState.value = RegisterSaverUiState.Error("Passwords do not match")
                return@launch
            }

            _uiState.value = RegisterSaverUiState.Loading
            val user = User(
                email = trimmedEmail,
                name = trimmedName,
                password = trimmedPassword,
                phone = trimmedPhone,
                type = "FoodSaver"
            )
            val result = repository.registerUser(user)
            if (result.isSuccess) {
                _uiState.value = RegisterSaverUiState.Success
            } else {
                _uiState.value = RegisterSaverUiState.Error(
                    result.exceptionOrNull()?.message ?: "Registration failed"
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = RegisterSaverUiState.Idle
    }
}