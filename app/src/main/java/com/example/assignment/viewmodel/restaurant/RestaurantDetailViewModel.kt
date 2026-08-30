package com.example.assignment.viewmodel.restaurant

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

data class RestaurantDetailUiState(
    val isLoading: Boolean = true,
    val restaurant: Restaurant? = null,
    val foods: List<FoodListing> = emptyList(),
    val errorMessage: String? = null
)

class RestaurantDetailViewModel(
    private val restaurantRepository: RestaurantRepository,
    private val foodRepository: FoodRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(
            RestaurantDetailUiState()
        )

    val uiState:
            StateFlow<RestaurantDetailUiState> =
        _uiState.asStateFlow()

    fun load(
        restaurantId: String
    ) {

        viewModelScope.launch {

            _uiState.value =
                RestaurantDetailUiState(
                    isLoading = true
                )

            try {

                val restaurant =
                    restaurantRepository
                        .getRestaurantById(
                            restaurantId
                        )

                if (restaurant == null) {

                    _uiState.value =
                        RestaurantDetailUiState(
                            isLoading = false,
                            errorMessage =
                                "Restaurant not found."
                        )

                    return@launch
                }

                val foods =
                    foodRepository
                        .getAllFoodListings()
                        .filter {
                            it.restaurant == restaurantId &&
                                    it.quantity > 0 &&
                                    !it.isPickupTimeEnded()
                        }

                _uiState.value =
                    RestaurantDetailUiState(
                        isLoading = false,
                        restaurant = restaurant,
                        foods = foods
                    )

            } catch (e: Exception) {

                _uiState.value =
                    RestaurantDetailUiState(
                        isLoading = false,
                        errorMessage =
                            e.message
                                ?: "Failed to load restaurant."
                    )
            }
        }
    }
}