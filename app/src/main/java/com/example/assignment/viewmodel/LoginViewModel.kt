package com.example.assignment.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.assignment.data.UserPreferencesManager
import com.example.assignment.data.repository.AuthRepository
import com.example.assignment.data.supabase.supabase
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val userPreferencesManager: UserPreferencesManager
) : ViewModel() {
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var message by mutableStateOf<String?>(null)
    var emailError by mutableStateOf<String?>(null)
        private set
    var passwordError by mutableStateOf<String?>(null)
        private set
    fun clearEmailError() { emailError = null }
    fun clearPasswordError() { passwordError = null }
    fun clearMessage() { message = null }

    fun login(
        onSuccess: (String) -> Unit
    ) {
        emailError = null
        passwordError = null
        message = null
        var valid = true

        if (email.isBlank()) {
            emailError = "Please enter your email address"
            valid = false
        }

        if (password.isBlank()) {
            passwordError = "Please enter your password"
            valid = false
        }


        // Stop if validation fails
        if (!valid) {
            return
        }

        viewModelScope.launch {
            isLoading = true
            try {
                val accountType = authRepository.login(
                    email = email.trim().lowercase(),
                    password = password
                )

                val userId = supabase.auth.currentUserOrNull()?.id ?: throw Exception("User ID not found after login")

                userPreferencesManager.saveUserData(accountType, userId)
                when (accountType) {
                    "FOOD_SAVER" -> { onSuccess("FOOD_SAVER") }
                    "FOOD_PROVIDER" -> { onSuccess("FOOD_PROVIDER") }
                    else -> { message = "Account type is not found" }
                }
            } catch (e: Exception) {
                when {
                    e.message?.contains("EMAIL_NOT_VERIFIED", ignoreCase = true) == true -> {
                        message = "Your email hasn't been verified yet."
                    }
                    e.message?.contains("Invalid login credentials", ignoreCase = true ) == true -> {
                        passwordError = "Invalid email or password"
                    }
                    else -> {
                        message = "Login failed. Please try again."
                    }
                }
            } finally {
                isLoading = false
            }
        }
    }


    // VIEWMODEL FACTORY
    companion object {
        fun Factory(
            authRepository: AuthRepository,
            userPreferencesManager: UserPreferencesManager
        ): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    LoginViewModel(
                        authRepository = authRepository,
                        userPreferencesManager = userPreferencesManager
                    )
                }
            }
    }
}