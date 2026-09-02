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

    fun login(onSuccess: (String) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            message = "Please fill in all fields"
            return
        }

        viewModelScope.launch {
            isLoading = true
            try {
                val accountType = authRepository.login(
                    email = email.trim().lowercase(),
                    password = password
                )
                val userId = supabase.auth.currentUserOrNull()?.id
                    ?: throw Exception("User ID not found after login")
                userPreferencesManager.saveUserData(
                    accountType,
                    userId
                )
                when (accountType) {
                    "FOOD_SAVER" -> { onSuccess("FOOD_SAVER") }
                    "FOOD_PROVIDER" -> { onSuccess("FOOD_PROVIDER") }
                    else -> { message = "Account type is not found" }
                }

            } catch (e: Exception) {
                message = when (e.message) {
                    "EMAIL_NOT_VERIFIED" ->
                        "Your email hasn't been verified yet. Please check your inbox."
                    else ->
                        "Login failed. Please check your email or password."
                }
            } finally {
                isLoading = false
            }
        }
    }

    fun clearMessage() {
        message = null
    }

    companion object {

        fun Factory(
            authRepository: AuthRepository,
            userPreferencesManager: UserPreferencesManager
        ): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {

                    LoginViewModel(
                        authRepository,
                        userPreferencesManager
                    )
                }
            }
    }
}