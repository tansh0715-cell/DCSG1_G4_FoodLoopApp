package com.example.assignment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.data.repository.FoodRepository
import com.example.assignment.model.FoodListing
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val foods: List<FoodListing> = emptyList(),
    val selectedCategoryIndex: Int = 0,
    val categories: List<String> = listOf("All", "Meals", "Bakery", "Snacks"),
    val errorMessage: String? = null
)
class HomeViewModel(
    private val repository: FoodRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    //store all the data fetched from the database without directly exposing it to the UI
    private var allFoods = emptyList<FoodListing>()

    fun loadAllFoods() {
        loadFoods(providerId = null)
    }
    fun loadProviderFoods(providerId: String) {
        loadFoods(providerId = providerId)
    }

    private fun loadFoods(providerId: String?) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, errorMessage = null)
            }

            try {
                allFoods = if (providerId.isNullOrBlank()) {
                    repository.getAllFoodListings()
                } else {
                    repository.getFoodListingByProvider(providerId)
                }

                applyCategoryFilter(_uiState.value.selectedCategoryIndex)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        foods = emptyList(),
                        errorMessage = e.message ?: "Failed to load food listings"
                    )
                }
            }
        }
    }

    fun refreshAllFoods() {
        loadAllFoods()
    }

    fun refreshProviderFoods(providerId: String) {
        loadProviderFoods(providerId)
    }

    fun onCategorySelected(index: Int) {
        _uiState.update { it.copy(selectedCategoryIndex = index) }
        applyCategoryFilter(index)
    }

    private fun applyCategoryFilter(index: Int) {
        val selectedCategory = _uiState.value.categories.getOrNull(index) ?: "All"

        val filtered = if (selectedCategory == "All") {
            allFoods
        } else {
            allFoods.filter { it.category.equals(selectedCategory, ignoreCase = true) }
        }

        _uiState.update {
            it.copy(
                isLoading = false,
                foods = filtered,
                errorMessage = null
            )
        }
    }
}