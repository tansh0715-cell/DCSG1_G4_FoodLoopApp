package com.example.assignment.viewmodel.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.data.repository.AuthRepository
import kotlinx.coroutines.launch

class ChangePasswordViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    var currentPassword by mutableStateOf("")
    var newPassword by mutableStateOf("")
    var confirmPassword by mutableStateOf("")

    var currentPasswordError by mutableStateOf<String?>(null)
    var newPasswordError by mutableStateOf<String?>(null)
    var confirmPasswordError by mutableStateOf<String?>(null)

    var isSaving by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var successMessage by mutableStateOf<String?>(null)
        private set

    fun clearErrors() {
        currentPasswordError = null
        newPasswordError = null
        confirmPasswordError = null
        errorMessage = null
        successMessage = null
    }

    fun clearMessages() {
        errorMessage = null
        successMessage = null
    }

    private fun validate(): Boolean {
        clearErrors()
        var valid = true
        if (newPassword.isBlank()) {
            newPasswordError = "New password is required"
            valid = false
        } else if (newPassword.length < 6) {
            newPasswordError = "Password must be at least 6 characters"
            valid = false
        }
        if (confirmPassword.isBlank()) {
            confirmPasswordError = "Please confirm new password"
            valid = false
        } else if (newPassword != confirmPassword) {
            confirmPasswordError = "Passwords do not match"
            valid = false
        }
        // currentPassword is optional for UX, but if filled verify length
        if (currentPassword.isNotBlank() && currentPassword.length < 6) {
            currentPasswordError = "Current password is too short"
            valid = false
        }
        return valid
    }

    fun changePassword(onSuccess: () -> Unit) {
        if (!validate()) return
        viewModelScope.launch {
            isSaving = true
            errorMessage = null
            successMessage = null
            try {
                // If current password provided, could verify via re-auth (optional)
                // For now directly update to new password using Supabase session
                authRepository.updatePassword(newPassword)
                successMessage = "Password updated successfully"
                onSuccess()
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to update password"
            } finally {
                isSaving = false
            }
        }
    }
}
