package com.example.assignment.viewmodel.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.assignment.data.repository.AuthRepository

class ChangePasswordViewModelFactory(
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChangePasswordViewModel::class.java)) {
            return ChangePasswordViewModel(
                authRepository = authRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
