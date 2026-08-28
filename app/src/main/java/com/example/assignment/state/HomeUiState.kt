package com.example.assignment.state

import com.example.assignment.data.repository.NearbyRestaurantRow
import com.example.assignment.model.FoodListing

data class HomeUiState(
    val isLoading: Boolean = false,
    val foods: List<FoodListing> = emptyList(),
    val selectedCategoryIndex: Int = 0,
    val categories: List<String> =
        listOf("All", "Meals", "Bakery", "Snacks"),

    val nearbyRestaurants: List<NearbyRestaurantRow> =
        emptyList(),

    val userLatitude: Double? = null,
    val userLongitude: Double? = null,
    val locationError: String? = null,

    val restaurantName: String? = null,
    val reservationCount: Int = 0,
    val totalFoodCount: Int = 0,
    val activeFoodCount: Int = 0,
    val errorMessage: String? = null
)
