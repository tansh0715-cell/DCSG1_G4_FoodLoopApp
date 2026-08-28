package com.example.assignment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.data.repository.FoodRepository
import com.example.assignment.data.repository.OrderRepository
import com.example.assignment.data.repository.RestaurantRepository
import com.example.assignment.model.FoodListing
import com.example.assignment.model.Order
import com.example.assignment.model.Restaurant
import com.example.assignment.model.inventoryModule.Food
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrderViewModel(
    private val repository: OrderRepository,
    private val currentUserId: String,
    private val foodRepository: FoodRepository,
    private val restaurantRepository: RestaurantRepository
) : ViewModel() {

    private val _orders =
        MutableStateFlow<List<Order>>(emptyList())

    val orders: StateFlow<List<Order>> =
        _orders.asStateFlow()

    private val _isLoading =
        MutableStateFlow(false)

    val isLoading: StateFlow<Boolean> =
        _isLoading.asStateFlow()

    //Order detail data
    private val _selectedFood = MutableStateFlow<FoodListing?>(null)
    val selectedFood: StateFlow<FoodListing?> = _selectedFood.asStateFlow()

    private val _selectedRestaurant = MutableStateFlow<Restaurant?>(null)
    val selectedRestaurant: StateFlow<Restaurant?> = _selectedRestaurant.asStateFlow()

    private val _selectedOrder = MutableStateFlow<Order?>(null)
    val selectedOrder: StateFlow<Order?> = _selectedOrder.asStateFlow()
    private val _isDetailLoading = MutableStateFlow(false)
    val isDetailLoading: StateFlow<Boolean> = _isDetailLoading.asStateFlow()

    fun loadConsumerOrders() {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                _orders.value =
                    repository.getConsumerOrders(currentUserId)
            }catch (e: Exception) {
                e.printStackTrace()
                _orders.value = emptyList()
            }finally {
                _isLoading.value = false
            }
        }
    }

    fun loadOrderById(
        orderId: String
    ){
        viewModelScope.launch {
            try {

                val orders =
                    repository.getConsumerOrders(
                        currentUserId
                    )

                _selectedOrder.value =
                    orders.firstOrNull {
                        it.id == orderId
                    }

            } catch (e: Exception) {

                e.printStackTrace()

                _selectedOrder.value = null
            }
        }
    }

    //load order detail
    fun loadOrderDetails(order: Order){
        viewModelScope.launch {
            _isDetailLoading.value = true
            try{
                //find the food listing using foodId
                _selectedFood.value = foodRepository.getFoodListingById(
                    id = order.foodId
                )

                //find the restaurnt using restaurantId
                _selectedRestaurant.value = restaurantRepository.getRestaurantById(
                    restaurantId = order.restaurantId
                )
            } catch (e: Exception){
                 e.printStackTrace()

                _selectedFood.value = null
                _selectedRestaurant.value = null
            } finally {
                _isDetailLoading.value = false
            }
        }
    }

    fun createOrder(
        foodId: String,
        quantity: Int,
        onSuccess: (Order) -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val createdOrder = repository.createOrder(
                    consumerId = currentUserId,
                    foodId = foodId,
                    quantity = quantity
                )

                onSuccess(
                    createdOrder
                 )

            } catch (e: Exception) {
                e.printStackTrace()
                onError(e)
            }
        }
    }

    fun loadProviderOrders() {
        viewModelScope.launch {
            _isLoading.value = true

            try {

                _orders.value =
                    repository.getProviderOrders(
                        currentUserId
                    )

            } catch (e: Exception) {

                e.printStackTrace()
                _orders.value = emptyList()

            } finally {

                _isLoading.value = false
            }
        }
    }

    fun markAsDone(orderId: String) {

        viewModelScope.launch {

            try {

                repository.markOrderDone(
                    orderId = orderId,
                    providerId = currentUserId
                )
                loadProviderOrders()

            } catch (e: Exception) {

                e.printStackTrace()
            }
        }
    }
}