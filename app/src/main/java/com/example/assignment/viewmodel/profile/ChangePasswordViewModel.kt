package com.example.assignment.viewmodel.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.data.repository.AuthRepository
import com.example.assignment.data.supabase.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
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

    var successMessage by mutableStateOf<String?>(null)
        private set

    val isConfirmMismatch: Boolean
        get() = confirmPassword.isNotBlank() && newPassword != confirmPassword

    fun clearErrors() {
        currentPasswordError = null
        newPasswordError = null
        confirmPasswordError = null
        successMessage = null
    }

    fun clearMessages() {
        successMessage = null
    }

    // Used to disable button in UI when mismatch



    private fun validate(): Boolean {
        clearErrors()
        var valid = true
        if (currentPassword.isBlank()) {
            currentPasswordError = "Current password is required"
            valid = false
        } else if (currentPassword.length < 6) {
            currentPasswordError = "Current password is too short"
            valid = false
        }
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
        } else if (newPassword == currentPassword) {
            confirmPasswordError = "New password must be different from current password"
            valid = false
        }
        return valid
    }

    fun changePassword(onSuccess: () -> Unit) {
        if (!validate()) return
        viewModelScope.launch {
            isSaving = true
            successMessage = null
            try {
                // Re-auth: verify current password against Supabase
                val email = supabase.auth.currentUserOrNull()?.email
                    ?: throw Exception("Not logged in")
                try {
                    supabase.auth.signInWith(Email) {
                        this.email = email
                        this.password = currentPassword
                    }
                } catch (e: Exception) {
                    currentPasswordError = "Current password is incorrect"
                    return@launch
                }
                // Current verified, now update to new password
                authRepository.updatePassword(newPassword)
                successMessage = "Password updated successfully"
                onSuccess()
            } catch (e: Exception) {

            } finally {
                isSaving = false
            }
        }
    }
}
