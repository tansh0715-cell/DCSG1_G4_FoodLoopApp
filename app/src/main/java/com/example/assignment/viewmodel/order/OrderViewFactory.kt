package com.example.assignment.viewmodel.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.assignment.data.repository.FoodRepository
import com.example.assignment.data.repository.OrderRepository
import com.example.assignment.data.repository.RestaurantRepository

class OrderViewModelFactory(
    private val orderRepository: OrderRepository,
    private val currentUserId: String,
    private val foodRepository: FoodRepository,
    private val restaurantRepository: RestaurantRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(OrderViewModel::class.java)) {

            return OrderViewModel(
                repository = orderRepository,
                currentUserId = currentUserId,
                foodRepository = foodRepository,
                restaurantRepository = restaurantRepository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}