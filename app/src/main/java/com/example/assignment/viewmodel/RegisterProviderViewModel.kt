package com.example.assignment.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.data.repository.UserRepository
import com.example.assignment.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class RegisterProviderUiState {
    object Idle : RegisterProviderUiState()
    object Loading : RegisterProviderUiState()
    object Success : RegisterProviderUiState()
    data class Error(val message: String) : RegisterProviderUiState()
}

class RegisterProviderViewModel(private val context: Context) : ViewModel() {
    private val repository = UserRepository(context)
    private val _uiState = MutableStateFlow<RegisterProviderUiState>(RegisterProviderUiState.Idle)
    val uiState: StateFlow<RegisterProviderUiState> = _uiState.asStateFlow()

    fun register(
        restaurant: String,
        email: String,
        phone: String,
        address: String,
        password: String,
        confirmPassword: String,
        imageUri: Uri?
    ) {
        viewModelScope.launch {
            val trimmedRestaurant = restaurant.trim()
            val trimmedEmail = email.trim()
            val trimmedPhone = phone.trim()
            val trimmedAddress = address.trim()
            val trimmedPassword = password.trim()
            val trimmedConfirm = confirmPassword.trim()

            if (trimmedRestaurant.isEmpty() || trimmedEmail.isEmpty() || trimmedPhone.isEmpty() || trimmedAddress.isEmpty() || trimmedPassword.isEmpty() || trimmedConfirm.isEmpty()) {
                _uiState.value = RegisterProviderUiState.Error("Please fill in all fields")
                return@launch
            }
            if (trimmedPassword != trimmedConfirm) {
                _uiState.value = RegisterProviderUiState.Error("Passwords do not match")
                return@launch
            }
            if (imageUri == null) {
                _uiState.value = RegisterProviderUiState.Error("Please upload a license photo")
                return@launch
            }

            _uiState.value = RegisterProviderUiState.Loading
            val user = User(
                email = trimmedEmail,
                name = trimmedRestaurant,
                password = trimmedPassword,
                phone = trimmedPhone,
                type = "FoodProvider",
                restaurant = trimmedRestaurant,
                address = trimmedAddress,
                licenseUri = imageUri.toString()
            )
            val result = repository.registerUser(user)
            if (result.isSuccess) {
                _uiState.value = RegisterProviderUiState.Success
            } else {
                _uiState.value = RegisterProviderUiState.Error(
                    result.exceptionOrNull()?.message ?: "Registration failed"
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = RegisterProviderUiState.Idle
    }
}