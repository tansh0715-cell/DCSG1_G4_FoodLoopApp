package com.example.assignment.viewmodel.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.data.repository.FoodRepository
import com.example.assignment.data.repository.OrderRepository
import com.example.assignment.data.repository.RestaurantRepository
import com.example.assignment.model.FoodListing
import com.example.assignment.model.Order
import com.example.assignment.model.Restaurant
import com.example.assignment.model.isPickupTimeEnded
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrderViewModel(
    private val repository: OrderRepository,
    private val currentUserId: String,
    private val foodRepository: FoodRepository,
    private val restaurantRepository: RestaurantRepository,
) : ViewModel() {

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _allNotificationOrders = MutableStateFlow<List<Order>>(emptyList())
    val allNotificationOrders: StateFlow<List<Order>> = _allNotificationOrders.asStateFlow()

    private val _providerNotificationOrders = MutableStateFlow<List<Order>>(emptyList())
    val providerNotificationOrders: StateFlow<List<Order>> = _providerNotificationOrders.asStateFlow()

    private val _isLoading = MutableStateFlow(false)

    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    //Order detail data
    private val _selectedFood = MutableStateFlow<FoodListing?>(null)
    val selectedFood: StateFlow<FoodListing?> = _selectedFood.asStateFlow()

    private val _selectedRestaurant = MutableStateFlow<Restaurant?>(null)
    val selectedRestaurant: StateFlow<Restaurant?> = _selectedRestaurant.asStateFlow()

    private val _selectedOrder = MutableStateFlow<Order?>(null)
    val selectedOrder: StateFlow<Order?> = _selectedOrder.asStateFlow()
    private val _isDetailLoading = MutableStateFlow(false)
    val isDetailLoading: StateFlow<Boolean> = _isDetailLoading.asStateFlow()

    private val _providerNotificationFoods = MutableStateFlow<List<FoodListing>>(
        emptyList()
    )
    val providerNotificationFoods: StateFlow<List<FoodListing>> =
        _providerNotificationFoods.asStateFlow()

    private val _foodByOrderId = MutableStateFlow<Map<String, FoodListing>>(emptyMap())
    val foodByOrderId: StateFlow<Map<String, FoodListing>> = _foodByOrderId.asStateFlow()
    private val _restaurantByOrderId = MutableStateFlow<Map<String, Restaurant>>(emptyMap())
    val restaurantByOrderId: StateFlow<Map<String, Restaurant>> = _restaurantByOrderId.asStateFlow()

    // Reservation History (picked up / completed)
    private val _historyOrders = MutableStateFlow<List<Order>>(emptyList())
    val historyOrders = _historyOrders.asStateFlow()

    private val _historyFoodByOrderId = MutableStateFlow<Map<String, FoodListing>>(emptyMap())
    val historyFoodByOrderId = _historyFoodByOrderId.asStateFlow()

    private val _historyRestaurantByOrderId = MutableStateFlow<Map<String, Restaurant>>(emptyMap())
    val historyRestaurantByOrderId = _historyRestaurantByOrderId.asStateFlow()

    private val _isHistoryLoading = MutableStateFlow(false)
    val isHistoryLoading = _isHistoryLoading.asStateFlow()

    fun loadConsumerOrders() {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val loadedOrders = repository.getConsumerOrders(
                    currentUserId
                )

                val activeOrders = loadedOrders.filter { order ->

                    order.status.equals(
                        "PENDING", ignoreCase = true
                    ) && !order.isPickupTimeEnded()
                }

                _orders.value = activeOrders

                loadConsumerRelatedData(
                    activeOrders
                )
            } catch (e: Exception) {
                e.printStackTrace()
                _orders.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun loadConsumerRelatedData(
        orders: List<Order>
    ) {
        val foods = mutableMapOf<String, FoodListing>()

        val restaurants = mutableMapOf<String, Restaurant>()

        orders.forEach { order ->

            foodRepository.getFoodListingById(order.foodId)?.let { food ->
                foods[order.id] = food
            }

            restaurantRepository.getRestaurantById(order.restaurantId)?.let { restaurant ->
                restaurants[order.id] = restaurant
            }
        }

        _foodByOrderId.value = foods
        _restaurantByOrderId.value = restaurants
    }

    fun loadOrderById(
        orderId: String
    ){
        viewModelScope.launch {
            try {
                // Try consumer pending orders first
                var found: Order? = null
                try {
                    val consumerOrders = repository.getConsumerOrders(currentUserId)
                    found = consumerOrders.firstOrNull { it.id == orderId }
                } catch (_: Exception) {}
                // Fallback to all consumer orders (includes COMPLETED)
                if (found == null) {
                    try {
                        val allConsumer = repository.getAllConsumerOrders(currentUserId)
                        found = allConsumer.firstOrNull { it.id == orderId }
                    } catch (_: Exception) {}
                }
                // Fallback to provider orders
                if (found == null) {
                    try {
                        val providerOrders = repository.getProviderOrders(currentUserId)
                        found = providerOrders.firstOrNull { it.id == orderId }
                    } catch (_: Exception) {}
                }
                // Last fallback direct fetch
                if (found == null) {
                    found = repository.getOrderById(orderId)
                }
                _selectedOrder.value = found

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
        paymentSuccess: Boolean,
        onSuccess: (Order) -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val createdOrder = repository.createOrder(
                    consumerId = currentUserId,
                    foodId = foodId,
                    quantity = quantity,
                    paymentSuccess = paymentSuccess
                )

                onSuccess(
                    createdOrder
                )

            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    //ADD
    fun loadProviderOrders() {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val loadedOrders =
                    repository.getProviderOrders(
                        currentUserId
                    )

                val activeOrders = loadedOrders.filter { order ->

                    order.status.equals(
                        "PENDING", ignoreCase = true
                    ) && !order.isPickupTimeEnded()
                }

                _orders.value = activeOrders

                // Load food and restaurant information
                // only for active orders.
                loadConsumerRelatedData(
                    activeOrders
                )

            } catch (e: Exception) {

                e.printStackTrace()
                _orders.value = emptyList()

            } finally {

                _isLoading.value = false
            }
        }
    }

    fun markAsDone(
        orderId: String,
        pickupCode: String,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        viewModelScope.launch {

            try {

                // Get the latest order from database
                val order = repository.getOrderById(orderId)
                    ?: throw IllegalArgumentException("Order not found.")

                val enteredCode =
                    pickupCode.trim().uppercase()

                val expectedCode =
                    order.pickupCode.trim().uppercase()

                // Validate BEFORE calling RPC
                if (enteredCode != expectedCode) {
                    throw IllegalArgumentException(
                        "Invalid pickup code."
                    )
                }

                // Correct code -> call RPC
                val updatedOrder =
                    repository.markOrderDone(
                        orderId = orderId,
                        providerId = currentUserId,
                        pickupCode = enteredCode
                    )

                // Make sure database really changed to COMPLETED
                if (
                    updatedOrder == null ||
                    !updatedOrder.status.equals(
                        "COMPLETED",
                        ignoreCase = true
                    )
                ) {
                    throw IllegalStateException(
                        "Order could not be completed."
                    )
                }

                // Reload provider orders
                loadProviderOrders()

                onSuccess()

            } catch (e: Exception) {

                withContext(
                    Dispatchers.Main.immediate
                ) {
                    onError(e)
                }
            }
        }
    }

    fun loadReservationHistory() {
        viewModelScope.launch {
            _isHistoryLoading.value = true
            try {

                val completedOrders = mutableListOf<Order>()
                var fetchError: Exception? = null

                // Try consumer all orders
                try {
                    val consumerAll = repository.getAllConsumerOrders(currentUserId)
                    completedOrders += consumerAll.filter {
                        it.status.equals("COMPLETED", ignoreCase = true)
                    }
                } catch (e: Exception) {
                    fetchError = e
                }

                // Provider Order
                try {
                    val providerOrders = repository.getProviderOrders(currentUserId)
                    val providerCompleted = providerOrders.filter {
                        it.status.equals("COMPLETED", ignoreCase = true)
                    }
                    // Add provider completed that not already in list (deduplicate by id)
                    val existingIds = completedOrders.map { it.id }.toSet()
                    completedOrders += providerCompleted.filter { it.id !in existingIds }
                } catch (e: Exception) {
                    if (completedOrders.isEmpty()) fetchError = e
                }



                // Sort by completedAt / createdAt descending (most recent first)
                val sorted = completedOrders.sortedByDescending { it.completedAt }

                _historyOrders.value = sorted

                // Load related food/restaurant for history
                val foods = mutableMapOf<String, FoodListing>()
                val restaurants = mutableMapOf<String, Restaurant>()
                sorted.forEach { order ->
                    try {
                        foodRepository.getFoodListingById(order.foodId)?.let { foods[order.id] = it }
                    } catch (_: Exception) {}
                    try {
                        restaurantRepository.getRestaurantById(order.restaurantId)?.let { restaurants[order.id] = it }
                    } catch (_: Exception) {}
                }
                _historyFoodByOrderId.value = foods
                _historyRestaurantByOrderId.value = restaurants

            } catch (e: Exception) {
                e.printStackTrace()
                _historyOrders.value = emptyList()
                _historyFoodByOrderId.value = emptyMap()
                _historyRestaurantByOrderId.value = emptyMap()
            } finally {
                _isHistoryLoading.value = false
            }
        }
    }

}