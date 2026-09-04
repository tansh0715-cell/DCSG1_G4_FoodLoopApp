package com.example.assignment.viewmodel.home

import android.content.Context
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
import java.time.Instant
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
)
class HomeViewModel(
    private val foodRepository: FoodRepository,
    private val restaurantRepository: RestaurantRepository,
    private val orderRepository: OrderRepository,
    private val authRepository: AuthRepository,
    private val context: Context
): ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val notificationPrefs =
        context.getSharedPreferences(
            "consumer_notification_state",
            Context.MODE_PRIVATE
        )

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
                allFoods = withContext(Dispatchers.IO) {
                    foodRepository.getFoodListingByProvider(providerId)
                }

                val totalFoodCount = allFoods.size

                val activeFoodCount =
                    allFoods.count { food ->
                        food.quantity > 0 &&
                                !food.isPickupTimeEnded()
                    }

                val restaurant = withContext(Dispatchers.IO) {
                    restaurantRepository.getRestaurantByProvider(providerId)
                }

                val provider = withContext(Dispatchers.IO) {
                    authRepository.getFoodProvider(providerId)
                }

                val providerOrders = withContext(Dispatchers.IO) {
                    orderRepository.getProviderOrders(providerId)
                }

                val reservationCount = providerOrders.size

                val now = Instant.now()
                val localNow = LocalTime.now()

                // Get provider notifications that have already been viewed
                val viewedProviderNotifications =
                    getViewedProviderNotifications(providerId)

                // Store IDs of notifications that currently exist
                val currentProviderNotificationIds =
                    mutableSetOf<String>()

                providerOrders.forEach { order ->

                    // -------------------------------
                    // New / pending reservation
                    // -------------------------------

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
                                        .between(
                                            createdAt,
                                            now
                                        )
                                        .toMinutes() in 0..1440

                                }.getOrDefault(false)

                    if (newOrderNotification) {

                        currentProviderNotificationIds.add(
                            "provider-order-${order.orderCode}"
                        )
                    }


                    // -------------------------------
                    // Pickup time approaching
                    // -------------------------------

                    val pickupNotification =
                        !order.status.equals(
                            "COMPLETED",
                            ignoreCase = true
                        ) &&
                                runCatching {

                                    val formatter =
                                        DateTimeFormatter.ofPattern(
                                            "h:mm a",
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
                                        Duration.between(
                                            localNow,
                                            pickupStart
                                        ).toMinutes()

                                    minutesUntil in 0..60

                                }.getOrDefault(false)

                    if (pickupNotification) {

                        currentProviderNotificationIds.add(
                            "provider-pickup-${order.orderCode}"
                        )
                    }
                }


                // -------------------------------
                // Low stock notification
                // -------------------------------

                allFoods.forEach { food ->

                    if (food.quantity <= 0) {

                        currentProviderNotificationIds.add(
                            "provider-low-stock-${food.id}"
                        )
                    }
                }


                // Only UNVIEWED notifications
                // should show the red dot.

                val hasProviderNotifications =
                    currentProviderNotificationIds.any { notificationId ->
                        !viewedProviderNotifications.contains(
                            notificationId
                        )
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

    private val providerNotificationPrefs =
        context.getSharedPreferences(
            "provider_notification_state",
            Context.MODE_PRIVATE
        )

    private fun getViewedProviderNotifications(
        providerId: String
    ): Set<String> {

        return providerNotificationPrefs
            .getStringSet(
                "viewed_$providerId",
                emptySet()
            )
            ?.toSet()
            ?: emptySet()
    }

    fun markProviderNotificationsViewed(
        providerId: String
    ) {
        viewModelScope.launch {

            try {

                val orders =
                    withContext(Dispatchers.IO) {
                        orderRepository.getProviderOrders(
                            providerId
                        )
                    }

                val foods =
                    withContext(Dispatchers.IO) {
                        foodRepository.getFoodListingByProvider(
                            providerId
                        )
                    }

                val currentNotificationIds =
                    mutableSetOf<String>()


                // -------------------------------
                // Order notifications
                // -------------------------------

                orders.forEach { order ->

                    if (
                        !order.status.equals(
                            "COMPLETED",
                            ignoreCase = true
                        )
                    ) {

                        currentNotificationIds.add(
                            "provider-order-${order.orderCode}"
                        )

                        currentNotificationIds.add(
                            "provider-pickup-${order.orderCode}"
                        )
                    }
                }


                // -------------------------------
                // Low stock notifications
                // -------------------------------

                foods.forEach { food ->

                    if (food.quantity <= 0) {

                        currentNotificationIds.add(
                            "provider-low-stock-${food.id}"
                        )
                    }
                }


                // Save viewed notification IDs
                markProviderNotificationIdsViewed(
                    providerId = providerId,
                    notificationIds = currentNotificationIds
                )


                // Immediately remove red dot
                _uiState.update {

                    it.copy(
                        providerNotificationsViewed = true,
                        hasProviderNotifications = false
                    )
                }

            } catch (e: Exception) {

                e.printStackTrace()

                _uiState.update {

                    it.copy(
                        providerNotificationsViewed = true,
                        hasProviderNotifications = false
                    )
                }
            }
        }
    }


    private fun markProviderNotificationIdsViewed(
        providerId: String,
        notificationIds: Set<String>
    ) {

        val current =
            getViewedProviderNotifications(
                providerId
            ).toMutableSet()

        current.addAll(
            notificationIds
        )

        providerNotificationPrefs
            .edit()
            .putStringSet(
                "viewed_$providerId",
                current
            )
            .apply()
    }

    private fun getViewedConsumerNotifications(
        userId: String
    ): Set<String> {

        return notificationPrefs
            .getStringSet(
                "viewed_$userId",
                emptySet()
            )
            ?.toSet()
            ?: emptySet()
    }

    private fun markConsumerNotificationIdsViewed(
        userId: String,
        notificationIds: Set<String>
    ) {

        val current =
            getViewedConsumerNotifications(userId)
                .toMutableSet()

        current.addAll(notificationIds)

        notificationPrefs
            .edit()
            .putStringSet(
                "viewed_$userId",
                current
            )
            .apply()
    }

    fun markConsumerNotificationsViewed() {

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

                val now =
                    java.time.Instant.now()

                val localNow =
                    LocalTime.now()

                val currentNotificationIds =
                    mutableSetOf<String>()

                orders.forEach { order ->

                    // Completed notification
                    if (
                        order.status.equals(
                            "COMPLETED",
                            ignoreCase = true
                        )
                    ) {

                        val completedAt =
                            order.completedAt
                                ?.let {
                                    runCatching {
                                        java.time.Instant.parse(it)
                                    }.getOrNull()
                                }

                        if (completedAt != null) {

                            val hoursPassed =
                                Duration.between(
                                    completedAt,
                                    now
                                ).toHours()

                            if (hoursPassed in 0..1440) {

                                currentNotificationIds.add(
                                    "completed-${order.orderCode}"
                                )
                            }
                        }
                    }

                    // Pickup notification
                    if (
                        !order.status.equals(
                            "COMPLETED",
                            ignoreCase = true
                        )
                    ) {

                        val pickupNotification =
                            runCatching {

                                val formatter =
                                    DateTimeFormatter.ofPattern(
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
                                    Duration.between(
                                        localNow,
                                        pickupStart
                                    ).toMinutes()

                                minutesUntil in 0..60

                            }.getOrDefault(false)

                        if (pickupNotification) {

                            currentNotificationIds.add(
                                "pickup-${order.orderCode}"
                            )
                        }
                    }
                }

                markConsumerNotificationIdsViewed(
                    userId = userId,
                    notificationIds =
                        currentNotificationIds
                )

                _uiState.update {

                    it.copy(
                        consumerNotificationsViewed = true,
                        hasNotifications = false
                    )
                }

            } catch (e: Exception) {

                _uiState.update {

                    it.copy(
                        consumerNotificationsViewed = true,
                        hasNotifications = false
                    )
                }
            }
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
                        it.quantity > 0 && !it.isPickupTimeEnded()
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

    fun deleteFood(
        foodId: String,
        providerId: String
    ) {
        viewModelScope.launch {
            try {

                val food = withContext(Dispatchers.IO) {
                    foodRepository.getFoodListingById(
                        id = foodId,
                        providerId = providerId
                    )
                }

                if (food == null) {
                    _uiState.update {
                        it.copy(
                            errorMessage = "Food listing not found"
                        )
                    }
                    return@launch
                }

                val isSoldOut = food.quantity <= 0
                val isExpired = food.isPickupTimeEnded()

                val canDelete = isSoldOut || isExpired

                if (!canDelete) {
                    _uiState.update {
                        it.copy(
                            errorMessage =
                                "Food can only be deleted when it is sold out or expired."
                        )
                    }
                    return@launch
                }

                withContext(Dispatchers.IO) {
                    foodRepository.deleteFoodListing(
                        foodId = foodId,
                        providerId = providerId
                    )
                }

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

        if (distance >= 200f) {
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

                val now =
                    java.time.Instant.now()

                val localNow =
                    LocalTime.now()

                val viewedIds =
                    getViewedConsumerNotifications(
                        userId
                    )

                val notificationIds =
                    mutableSetOf<String>()

                orders.forEach { order ->

                    // --------------------------------
                    // COMPLETED FOOD NOTIFICATION
                    // --------------------------------

                    if (
                        order.status.equals(
                            "COMPLETED",
                            ignoreCase = true
                        )
                    ) {

                        val completedAt =
                            order.completedAt
                                ?.let {
                                    runCatching {
                                        java.time.Instant.parse(it)
                                    }.getOrNull()
                                }

                        if (completedAt != null) {

                            val hoursPassed =
                                Duration.between(
                                    completedAt,
                                    now
                                ).toHours()

                            if (hoursPassed in 0..1440) {

                                notificationIds.add(
                                    "completed-${order.orderCode}"
                                )
                            }
                        }
                    }

                    // --------------------------------
                    // PICKUP TIME NOTIFICATION
                    // --------------------------------

                    if (
                        !order.status.equals(
                            "COMPLETED",
                            ignoreCase = true
                        )
                    ) {

                        val pickupNotification =
                            runCatching {

                                val formatter =
                                    DateTimeFormatter.ofPattern(
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
                                    Duration.between(
                                        localNow,
                                        pickupStart
                                    ).toMinutes()

                                minutesUntil in 0..60

                            }.getOrDefault(false)

                        if (pickupNotification) {

                            notificationIds.add(
                                "pickup-${order.orderCode}"
                            )
                        }
                    }
                }

                // Only notifications that have NOT
                // been viewed should create the red dot.

                val hasUnreadNotification =
                    notificationIds.any {
                        !viewedIds.contains(it)
                    }

                _uiState.update {

                    it.copy(
                        hasNotifications =
                            hasUnreadNotification,

                        consumerNotificationsViewed =
                            !hasUnreadNotification
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