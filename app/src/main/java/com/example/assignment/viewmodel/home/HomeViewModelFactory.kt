package com.example.assignment.viewmodel.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.assignment.data.repository.AuthRepository
import com.example.assignment.data.repository.FoodRepository
import com.example.assignment.data.repository.OrderRepository
import com.example.assignment.data.repository.RestaurantRepository

class HomeViewModelFactory(
    private val foodRepository: FoodRepository,
    private val restaurantRepository: RestaurantRepository,
    private val orderRepository: OrderRepository,
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {

            return HomeViewModel(
                foodRepository = foodRepository,
                restaurantRepository = restaurantRepository,
                orderRepository = orderRepository,
                 authRepository = authRepository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}