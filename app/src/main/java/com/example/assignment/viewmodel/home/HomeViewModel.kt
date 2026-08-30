package com.example.assignment.viewmodel.home

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.data.repository.AuthRepository
import com.example.assignment.data.repository.FoodRepository
import com.example.assignment.data.repository.NearbyRestaurantRow
import com.example.assignment.data.repository.OrderRepository
import com.example.assignment.data.repository.RestaurantRepository
import com.example.assignment.data.supabase.supabase
import com.example.assignment.model.FoodListing
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

data class  HomeUiState(
    val isLoading: Boolean = false,
    val foods: List<FoodListing> = emptyList(),

    val hasNotifications: Boolean = false,
    val consumerNotificationsViewed: Boolean = false,

    val hasProviderNotifications: Boolean = false,
    val providerNotificationsViewed: Boolean = false,
    val providerNotificationPopupShown: Boolean = false,

    val selectedCategoryIndex: Int = 0,
    val categories: List<String> = listOf("All", "Meals", "Bakery", "Snacks"),

    //Customer side nearby restaurant
    val nearbyRestaurants: List<NearbyRestaurantRow> = emptyList(),

    val userLatitude: Double? = null,
    val userLongitude: Double? = null,
    val locationError: String? = null,

    //provider restaurant information
    val restaurantName: String? = null,
    //num of provider orders/resevations
    val reservationCount: Int = 0,

    //provider dashboard statistics
    val totalFoodCount: Int = 0,
    val activeFoodCount: Int = 0,
    val errorMessage: String? = null,

    //notification popup (inside app)
    val notificationPopupShown: Boolean = false
)
class HomeViewModel(
    private val foodRepository: FoodRepository,
    private val restaurantRepository: RestaurantRepository,
    private val orderRepository: OrderRepository,
    private val authRepository: AuthRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // Store all food fetched from Supabase
    // The UI only receives the filtered result through uiState
    private var allFoods = emptyList<FoodListing>()

    fun loadAllFoods() {
        loadFoods(providerId = null)
    }

    fun loadProviderHome(providerId: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            try {
                // Get foods belonging to this provider
                allFoods = foodRepository
                    .getFoodListingByProvider(providerId)

                // Calculate Provider Dashboard statictics
                val totalFoodCount = allFoods.size
                val activeFoodCount = allFoods.count{ food ->
                    food.quantity > 0
                }

                // Get provider's restaurant
                val restaurant = restaurantRepository
                    .getRestaurantByProvider(providerId)
                val provider = authRepository
                    .getFoodProvider(providerId)

                // Get provider orders and use the count
                val providerOrders = orderRepository
                    .getProviderOrders(
                        providerId
                    )
                val reservationCount = providerOrders.size

                val now = java.time.Instant.now()
                val localNow = LocalTime.now()

                val hasProviderNotifications =
                    providerOrders.any { order ->

                        // New / pending reservation
                        val newOrderNotification =
                            !order.status.equals(
                                "COMPLETED",
                                ignoreCase = true
                            ) &&
                                    runCatching {
                                        val createdAt =
                                            java.time.Instant.parse(
                                                order.createdAt
                                            )

                                        Duration
                                            .between(createdAt, now)
                                            .toMinutes() in 0..1440

                                    }.getOrDefault(false)


                        // Pickup time approaching
                        val pickupNotification = !order.status.equals(
                            "COMPLETED", ignoreCase = true
                        ) && runCatching {

                            val formatter = DateTimeFormatter.ofPattern(
                                "h:mm a", Locale.ENGLISH
                            )

                            val pickupStart = LocalTime.parse(
                                order.pickupTime.substringBefore("-").trim(), formatter
                            )

                            val minutesUntil = Duration.between(
                                    localNow, pickupStart
                                ).toMinutes()

                            minutesUntil in 0..60

                        }.getOrDefault(false)

                        newOrderNotification || pickupNotification

                    } || allFoods.any {
                        it.quantity <= 0
                    }

                //Apply currently selected category
                val selectedCategory =
                    _uiState.value.categories.getOrNull(
                        _uiState.value.selectedCategoryIndex
                    )?: "All"
                val filteredFoods =
                    if(selectedCategory == "All") {
                        allFoods
                    }else{
                        allFoods.filter {
                            it.category.equals(
                                selectedCategory,
                                ignoreCase = true
                            )
                        }
                    }

                //update everything together
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        foods = filteredFoods,
                        restaurantName = provider?.restaurantName?: restaurant?.name,
                        reservationCount = reservationCount,
                        totalFoodCount = totalFoodCount,
                        activeFoodCount = activeFoodCount,
                        hasProviderNotifications = hasProviderNotifications,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        foods = emptyList(),
                        reservationCount = 0,
                        totalFoodCount = 0,
                        activeFoodCount = 0,
                        errorMessage = e.message ?: "Failed to load provider dashboard"
                    )
                }
            }
        }
    }
    fun loadProviderFoods(providerId: String) {
        loadFoods(providerId)
    }

    fun markProviderNotificationsViewed() {
        _uiState.update {
            it.copy(
                providerNotificationsViewed = true
            )
        }
    }
    fun markProviderNotificationPopupShown() {
        _uiState.update {
            it.copy(
                providerNotificationPopupShown = true
            )
        }
    }

    fun markConsumerNotificationsViewed() {
        _uiState.update {
            it.copy(
                consumerNotificationsViewed = true
            )
        }
    }

    fun markConsumerNotificationPopupShown() {
        _uiState.update {
            it.copy(
                notificationPopupShown = true
            )
        }
    }

    private fun loadFoods(providerId: String?) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, errorMessage = null)
            }

            try {
                allFoods = if (providerId.isNullOrBlank()) {
                    foodRepository
                        .getAllFoodListings()
                        .filter { food ->
                            food.quantity > 0 && !food.isPickupTimeEnded()
                        }
                } else {
                    foodRepository.getFoodListingByProvider(providerId)
                }

                //Calculate provider statistics
                val totalFoodCount =
                    if(providerId.isNullOrBlank()){
                        allFoods.size
                    }else{
                        allFoods.size
                    }

                val activeFoodCount =
                    allFoods.count {
                        it.quantity > 0
                    }
                applyCategoryFilter(_uiState.value.selectedCategoryIndex)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        totalFoodCount = totalFoodCount,
                        activeFoodCount = activeFoodCount,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        foods = emptyList(),
                        totalFoodCount = 0,
                        activeFoodCount = 0,
                        errorMessage = e.message ?: "Failed to load food listings"
                    )
                }
            }
        }
    }

    //refresh
    fun refreshAllFoods() {
        loadAllFoods()
    }
    fun refreshProviderFoods(providerId: String) {
        loadProviderFoods(providerId)
    }

    fun deleteFood(
        foodId: String,
        providerId: String
    ) {
        viewModelScope.launch {
            try {

                val food = foodRepository.getFoodListingById(
                    id = foodId,
                    providerId = providerId
                )

                if (food == null) {
                    _uiState.update {
                        it.copy(
                            errorMessage = "Food listing not found"
                        )
                    }
                    return@launch
                }
                foodRepository.deleteFoodListing(
                    foodId = foodId,
                    providerId = providerId
                )

                loadProviderHome(providerId)

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage =
                            e.message ?: "Failed to delete food"
                    )
                }
            }
        }
    }

    fun onCategorySelected(index: Int) {
        _uiState.update {
            it.copy(selectedCategoryIndex = index)
        }
        applyCategoryFilter(index)
    }

    private fun applyCategoryFilter(index: Int) {
        val selectedCategory = _uiState.value.categories.getOrNull(index) ?: "All"

        val filtered = if (selectedCategory == "All") {
            allFoods
        } else {
            allFoods.filter { it.category.equals(selectedCategory, ignoreCase = true) }
        }

        _uiState.update {
            it.copy(
                foods = filtered,
                errorMessage = null
            )
        }
    }

    private var lastNearbyLatitude: Double? = null
    private var lastNearbyLongitude: Double? = null

    fun updateUserLocation(
        latitude: Double,
        longitude: Double
    ) {
        println(
            "LOCATION DEBUG → lat=$latitude, lon=$longitude"
        )

        _uiState.update {
            it.copy(
                userLatitude = latitude,
                userLongitude = longitude,
                locationError = null
            )
        }

        val oldLat = lastNearbyLatitude
        val oldLon = lastNearbyLongitude

        // First location update
        if (oldLat == null || oldLon == null) {

            println(
                "NEARBY DEBUG → First location update, loading restaurants"
            )

            loadNearbyRestaurants(
                latitude,
                longitude
            )

            return
        }

        val distance = Location("").apply {
            this.latitude = oldLat
            this.longitude = oldLon
        }.distanceTo(
            Location("").apply {
                this.latitude = latitude
                this.longitude = longitude
            }
        )

        println(
            "NEARBY DEBUG → Moved ${distance}m"
        )

        if (distance >= 200f) {

            println(
                "NEARBY DEBUG → More than 200m, refreshing"
            )

            loadNearbyRestaurants(
                latitude,
                longitude
            )
        }
    }

    private fun loadNearbyRestaurants(
        latitude: Double,
        longitude: Double
    ) {
        lastNearbyLatitude = latitude
        lastNearbyLongitude = longitude

        viewModelScope.launch {

            try {
                val restaurants =
                    withContext(Dispatchers.IO) {
                        restaurantRepository.getNearbyRestaurants(
                            latitude = latitude,
                            longitude = longitude
                        )
                    }

                _uiState.update {
                    it.copy(
                        nearbyRestaurants = restaurants
                            .filter {
                                it.distanceMeters <= 10_000
                            }
                            .sortedBy {
                                it.distanceMeters
                            },
                        locationError = null
                    )
                }

            } catch (e: Exception) {

                println(
                    "NEARBY DEBUG → ERROR: ${e.message}"
                )

                e.printStackTrace()

                _uiState.update {
                    it.copy(
                        nearbyRestaurants = emptyList(),
                        locationError =
                            e.message
                                ?: "Unable to load nearby restaurants"
                    )
                }
            }
        }
    }

    fun loadConsumerNotificationState() {
        viewModelScope.launch {

            try {

                val userId =
                    supabase.auth.currentUserOrNull()?.id
                        ?: return@launch

                val orders =
                    withContext(Dispatchers.IO) {
                        orderRepository.getAllConsumerOrders(
                            userId
                        )
                    }

                val now = java.time.Instant.now()
                val localNow = LocalTime.now()

                val hasNotification =
                    orders.any { order ->

                        val completedNotification =
                            if (
                                order.status.equals(
                                    "COMPLETED",
                                    ignoreCase = true
                                )
                            ) {
                                order.completedAt
                                    ?.let {
                                        runCatching {
                                            java.time.Instant.parse(it)
                                        }.getOrNull()
                                    }
                                    ?.let { completedAt ->
                                        Duration
                                            .between(
                                                completedAt,
                                                now
                                            )
                                            .toHours() in 0..1440
                                    } == true
                            } else {
                                false
                            }

                        val pickupNotification =
                            if (
                                !order.status.equals(
                                    "COMPLETED",
                                    ignoreCase = true
                                )
                            ) {

                                runCatching {

                                    val formatter =
                                        DateTimeFormatter
                                            .ofPattern(
                                                "hh:mm a",
                                                Locale.ENGLISH
                                            )

                                    val pickupStart =
                                        LocalTime.parse(
                                            order.pickupTime
                                                .substringBefore("-")
                                                .trim(),
                                            formatter
                                        )

                                    val minutesUntil =
                                        Duration
                                            .between(
                                                localNow,
                                                pickupStart
                                            )
                                            .toMinutes()

                                    minutesUntil in 0..60

                                }.getOrDefault(false)

                            } else {
                                false
                            }

                        completedNotification ||
                                pickupNotification
                    }

                _uiState.update {
                    it.copy(
                        hasNotifications =
                            hasNotification
                    )
                }

            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        hasNotifications = false
                    )
                }
            }
        }
    }
}