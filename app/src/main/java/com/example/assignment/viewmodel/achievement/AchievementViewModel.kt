package com.example.assignment.viewmodel.achievement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.data.repository.AchievementRepository
import com.example.assignment.model.achievementModule.Achievement
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AchievementProgress(
    val achievement: Achievement,
    val current: Int
)

class AchievementViewModel(
    private val repository: AchievementRepository,
    private val currentUserId: String
) : ViewModel() {

    private val _achievements =
        MutableStateFlow<List<AchievementProgress>>(emptyList())

    val achievements: StateFlow<List<AchievementProgress>> =
        _achievements.asStateFlow()


    private val _isLoading =
        MutableStateFlow(false)

    val isLoading: StateFlow<Boolean> =
        _isLoading.asStateFlow()


    private val _error =
        MutableStateFlow<String?>(null)

    val error: StateFlow<String?> =
        _error.asStateFlow()


    fun loadAchievements() {

        viewModelScope.launch {

            _isLoading.value = true
            _error.value = null

            try {

                // Get all achievements
                val achievements =
                    repository.getAchievements()

                // Calculate total meals directly
                // from the user's orders
                val totalMeals =
                    repository.getTotalMealsSaved(
                        currentUserId
                    )


                // Apply the same meal count
                // to every achievement
                _achievements.value =
                    achievements.map { achievement ->

                        AchievementProgress(
                            achievement = achievement,
                            current = totalMeals
                        )
                    }

            } catch (e: Exception) {

                e.printStackTrace()

                _error.value =
                    e.message
                        ?: "Failed to load achievements"

            } finally {

                _isLoading.value = false
            }
        }
    }
}