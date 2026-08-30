package com.example.assignment.viewmodel.food

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.data.repository.FoodRepository
import com.example.assignment.data.repository.RestaurantRepository
import com.example.assignment.model.FoodListing
import com.example.assignment.model.Restaurant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FoodDetailUiState(
    val isLoading: Boolean = true,
    val food: FoodListing? = null,
    val restaurant: Restaurant? = null,
    val errorMessage: String? = null
)

class FoodDetailViewModel(
    private val foodRepository: FoodRepository,
    private val restaurantRepository: RestaurantRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(
            FoodDetailUiState()
        )

    val uiState:
            StateFlow<FoodDetailUiState> =
        _uiState.asStateFlow()

    fun load(
        foodId: String
    ) {

        viewModelScope.launch {

            try {

                val food =
                    foodRepository
                        .getFoodListingById(
                            id = foodId
                        )

                if (food == null) {

                    _uiState.value =
                        FoodDetailUiState(
                            isLoading = false,
                            errorMessage =
                                "Food listing not found."
                        )

                    return@launch
                }

                val restaurant =
                    food.restaurant?.let {
                        restaurantRepository
                            .getRestaurantById(it)
                    }

                _uiState.value =
                    FoodDetailUiState(
                        isLoading = false,
                        food = food,
                        restaurant = restaurant
                    )

            } catch (e: Exception) {

                _uiState.value =
                    FoodDetailUiState(
                        isLoading = false,
                        errorMessage =
                            e.message
                                ?: "Failed to load food."
                    )
            }
        }
    }
}