package com.example.assignment.viewmodel.achievement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.data.repository.AchievementRepository
import com.example.assignment.model.achievementModule.Achievement
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class AchievementViewModel(
    private val repository: AchievementRepository,
    private val currentUserId: String
) : ViewModel() {

    private val _achievements = MutableStateFlow<List<Achievement>>(emptyList())
    val achievements = _achievements.asStateFlow()

    private val _currentProgress = MutableStateFlow(0)
    val currentProgress = _currentProgress.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()


    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()


    fun loadAchievements() {

        viewModelScope.launch {

            _isLoading.value = true
            _error.value = null

            try {

                // Get all achievements
                _achievements.value = repository.getAchievements()

                _currentProgress.value  =
                    repository.getTotalMealsSaved(
                        currentUserId
                    )


            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = e.message ?: "Failed to load achievements"

            } finally {
                _isLoading.value = false
            }
        }
    }
}