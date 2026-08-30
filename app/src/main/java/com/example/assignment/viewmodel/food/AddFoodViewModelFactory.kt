package com.example.assignment.viewmodel.food

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.assignment.data.repository.FoodRepository
import com.example.assignment.data.repository.RestaurantRepository

class AddFoodViewModelFactory(
    private val repository: FoodRepository,
    private val restaurantRepository: RestaurantRepository,
    private val providerId: String
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(AddFoodViewModel::class.java)) {

            return AddFoodViewModel(
                repository = repository,
                restaurantRepository = restaurantRepository,
                currentProviderId = providerId
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}