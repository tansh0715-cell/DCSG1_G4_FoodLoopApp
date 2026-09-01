package com.example.assignment.viewmodel.achievement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.assignment.data.repository.AchievementRepository

class AchievementViewModelFactory(
    private val repository: AchievementRepository,
    private val currentUserId: String
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                AchievementViewModel::class.java
            )
        ) {

            return AchievementViewModel(
                repository = repository,
                currentUserId = currentUserId
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}