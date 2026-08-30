package com.example.assignment.viewmodel.food

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.assignment.data.repository.FoodRepository
import com.example.assignment.data.repository.RestaurantRepository

class FoodDetailViewModelFactory(
    private val foodRepository: FoodRepository,
    private val restaurantRepository: RestaurantRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                FoodDetailViewModel::class.java
            )
        ) {

            return FoodDetailViewModel(
                foodRepository,
                restaurantRepository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}