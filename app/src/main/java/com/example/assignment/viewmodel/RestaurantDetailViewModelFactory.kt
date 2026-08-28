package com.example.assignment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.assignment.data.repository.FoodRepository
import com.example.assignment.data.repository.RestaurantRepository

class RestaurantDetailViewModelFactory(
    private val restaurantRepository:
    RestaurantRepository,

    private val foodRepository:
    FoodRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                RestaurantDetailViewModel::class.java
            )
        ) {

            return RestaurantDetailViewModel(
                restaurantRepository =
                    restaurantRepository,
                foodRepository =
                    foodRepository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}