package com.example.assignment.viewmodel.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.data.repository.FoodRepository
import com.example.assignment.data.repository.OrderRepository
import com.example.assignment.data.repository.RestaurantRepository
import com.example.assignment.model.FoodListing
import com.example.assignment.model.Order
import com.example.assignment.model.Restaurant
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
    val historyOrders: StateFlow<List<Order>> = _historyOrders.asStateFlow()

    private val _historyFoodByOrderId = MutableStateFlow<Map<String, FoodListing>>(emptyMap())
    val historyFoodByOrderId: StateFlow<Map<String, FoodListing>> = _historyFoodByOrderId.asStateFlow()

    private val _historyRestaurantByOrderId = MutableStateFlow<Map<String, Restaurant>>(emptyMap())
    val historyRestaurantByOrderId: StateFlow<Map<String, Restaurant>> = _historyRestaurantByOrderId.asStateFlow()

    private val _isHistoryLoading = MutableStateFlow(false)
    val isHistoryLoading: StateFlow<Boolean> = _isHistoryLoading.asStateFlow()

    fun loadConsumerOrders() {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val loadedOrders =
                    repository.getConsumerOrders(
                        currentUserId
                    )
                _orders.value = loadedOrders
                loadConsumerRelatedData(
                    loadedOrders
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
    fun loadConsumerNotificationOrders() {
        viewModelScope.launch {
            try {
                val notificationOrders =
                    repository.getAllConsumerOrders(
                        currentUserId
                    )
                _allNotificationOrders.value =
                    notificationOrders

                loadConsumerRelatedData(
                    notificationOrders
                )

            } catch (e: Exception) {
                e.printStackTrace()
                _allNotificationOrders.value =
                    emptyList()
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

    fun loadProviderOrders() {
        viewModelScope.launch {
            _isLoading.value = true

            try {

                val loadedOrders =
                    repository.getProviderOrders(
                        currentUserId
                    )

                _orders.value = loadedOrders

                // Load food information for each order
                loadConsumerRelatedData(
                    loadedOrders
                )

            } catch (e: Exception) {

                e.printStackTrace()
                _orders.value = emptyList()

            } finally {

                _isLoading.value = false
            }
        }
    }
    fun loadProviderNotificationOrders() {
        viewModelScope.launch {
            try {
                _providerNotificationOrders.value =
                    repository.getProviderOrders(
                        currentUserId
                    )
                _providerNotificationFoods.value =
                    foodRepository
                        .getFoodListingByProvider(
                            currentUserId
                        )
            } catch (e: Exception) {
                e.printStackTrace()

                _providerNotificationOrders.value =
                    emptyList()
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

                repository.markOrderDone(
                    orderId = orderId,
                    providerId = currentUserId,
                    pickupCode = pickupCode
                )
                loadProviderOrders()
                onSuccess()

            } catch (e: Exception) {
                withContext(Dispatchers.Main.immediate) {
                    onError(e)
                }
            }
        }
    }

    fun loadReservationHistory() {
        viewModelScope.launch {
            _isHistoryLoading.value = true
            try {
                // Fetch both consumer (all) and provider orders, then filter COMPLETED
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

                // Try provider orders as well (handles provider role or mixed)
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

                // Fallback: if both RPCs failed or empty, try getConsumerOrders and filter
                if (completedOrders.isEmpty() && fetchError != null) {
                    try {
                        val consumerPending = repository.getConsumerOrders(currentUserId)
                        completedOrders += consumerPending.filter {
                            it.status.equals("COMPLETED", ignoreCase = true)
                        }
                    } catch (_: Exception) {}
                }

                // Sort by completedAt / createdAt descending (most recent first)
                val sorted = completedOrders.sortedByDescending { it.completedAt ?: it.createdAt }

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

    fun refreshReservationHistory() = loadReservationHistory()
}