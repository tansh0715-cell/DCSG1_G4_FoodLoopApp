package com.example.assignment.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.data.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var toastMessage by mutableStateOf<String?>(null)
    private val authRepository = AuthRepository()

    fun login(onSuccess: (String) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            toastMessage = "Please fill in all fields"
            return
        }

        viewModelScope.launch {
            isLoading = true
            try {
                val accountType = authRepository.login(
                    email = email.trim().lowercase(),
                    password = password
                )
                when (accountType) {
                    "FOOD_SAVER" -> onSuccess("FOOD_SAVER")
                    "FOOD_PROVIDER" -> onSuccess("FOOD_PROVIDER")
                    else -> {
                        toastMessage = "Account type is not found"
                    }
                }
            } catch (e: Exception) {
                // 保留了你原代码里的 Log.e
                Log.e("Login", "Login failed", e)
                toastMessage = "Login failed. Please check your email or password."
            } finally {
                isLoading = false
            }
        }
    }

    fun clearToast() {
        toastMessage = null
    }
}