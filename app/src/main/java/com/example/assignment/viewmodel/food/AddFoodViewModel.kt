package com.example.assignment.viewmodel.food

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.data.repository.FoodRepository
import com.example.assignment.data.repository.RestaurantRepository
import com.example.assignment.model.FoodListing
import com.example.assignment.state.AddFoodEvent
import com.example.assignment.state.AddFoodUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

class AddFoodViewModel(
    private val repository: FoodRepository,
    private val restaurantRepository: RestaurantRepository,
    private val currentProviderId: String
): ViewModel() {
    var editingFoodId: String? = null
        private set
    //other classes can read editingFoodId but only AddFoodViewModel can change it

    //record UI state
    //all form values and validation errors are stored inside AddFoodUiState
    private val _uiState = MutableStateFlow(AddFoodUiState())
    val uiState: StateFlow<AddFoodUiState> = _uiState.asStateFlow()

    //prevent duplicate toast
    private val _uiEvent = Channel<AddFoodEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    val categories = listOf("Meals", "Bakery", "Snacks")

    //handle events from the UI
    fun onNameChange(newValue: String){
        //copy existing state & only modify the changed parts
        _uiState.update {
            it.copy(
                foodName = newValue,
                nameError = null //if user retry --> clear error msg
            )
        }
    }
    fun onCategoryChange(newCategory: String){
        _uiState.update {
            it.copy(
                selectedCategory = newCategory,
                categoryError = null
            )
        }
    }
    fun onQtyChange(newValue: String) {

        val numericValue = newValue.filter { it.isDigit() }

        if (numericValue.isBlank()) {
            _uiState.update {
                it.copy(
                    quantity = "",
                    qtyError = null
                )
            }
            return
        }

        val value = numericValue.toIntOrNull()

        if (value != null && value <= 100) {
            _uiState.update {
                it.copy(
                    quantity = value.toString(),
                    qtyError = null
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    qtyError = "Quantity must be between 1 and 100"
                )
            }
        }
    }
    fun onDescriptionChange(newValue: String){
        _uiState.update {
            it.copy(
                description = newValue
            )
        }
    }
    fun onPriceChange(newValue: String) {

        // Only allow numbers with up to 2 decimal places
        val cleaned = newValue
            .replace(",", ".")
            .filter { it.isDigit() || it == '.' }

        // Prevent more than one decimal point
        if (cleaned.count { it == '.' } > 1) {
            return
        }

        // Maximum 2 decimal places
        val decimalPart = cleaned.substringAfter('.', "")
        if (decimalPart.length > 2) {
            return
        }

        val value = cleaned.toDoubleOrNull()

        if (value == null || value <= 100.0) {

            _uiState.update {
                it.copy(
                    originalPrice = cleaned,
                    priceError = null
                )
            }

        } else {

            _uiState.update {
                it.copy(
                    priceError = "Price cannot exceed RM100.00"
                )
            }
        }
    }
    fun onDiscountChange(newValue: String) {

        val numericValue = newValue
            .filter { it.isDigit() }

        if (numericValue.isEmpty()) {
            _uiState.update {
                it.copy(
                    selectedDiscount = "",
                )
            }
            return
        }

        val value = numericValue.toIntOrNull() ?: return

        if (value <= 100) {
            _uiState.update {
                it.copy(
                    selectedDiscount = value.toString()
                )
            }
        }
    }

    fun onPickupTimeChange(newValue: String){
        _uiState.update {
            it.copy(
                pickupTime = newValue,
                pickupTimeError = null
            )
        }
    }

    fun validatePickupTimeInput(): Boolean {
        val pickupTime = _uiState.value.pickupTime

        val error = validatePickupTime(pickupTime)

        _uiState.update {
            it.copy(
                pickupTimeError = error
            )
        }

        return error == null
    }

    //the same food ID is preserved so the later upsert updates instead of inserting another card
    fun loadFoodForEdit(foodId: String){
        editingFoodId = foodId
        viewModelScope.launch {
            try {
                val food = withContext(Dispatchers.IO) {
                    repository.getFoodListingById(
                        id = foodId,
                        providerId = currentProviderId
                    )
                }
                if(food!=null) {
                    val canEdit = food.quantity <= 0 || food.isPickupTimeEnded()

                    if (!canEdit) {
                        _uiEvent.trySend(
                            AddFoodEvent.ShowToast(
                                "This food can only be edited after it is sold out or the pickup time has ended."
                            )
                        )

                        return@launch
                    }

                    _uiState.update {
                        it.copy(
                            foodName = food.name,
                            description = food.description ?: "",
                            selectedCategory = food.category,
                            quantity = food.quantity.toString(),
                            pickupTime = food.pickupTime,
                            originalPrice = food.originalPrice.toString(),
                            selectedDiscount = food.discountPercentage.toString(),
                            //Existing Supabase Image
                            imageUrl = food.imageUrl,

                            //clear local image, user can choose a new image if needed
                            imageUri = null,

                            nameError = null,
                            categoryError = null,
                            qtyError = null,
                            priceError = null,
                            pickupTimeError = null,
                            imageError = null
                        )
                    }
                }

            } catch (e: Exception){
                _uiEvent.trySend(
                    AddFoodEvent.ShowToast(
                        e.message ?: "Failed to load food"
                    )
                )
            }
        }
    }

    fun submitFood(){
        val currentState = _uiState.value
        var isValid = true

        var newNameError: String? = null
        var newCategoryError: String? = null
        var newQtyError: String? = null
        var newPriceError: String? = null
        var newPickupTimeError: String? = null

        if(currentState.foodName.isBlank()){
            newNameError = "Please enter a food name"
            isValid = false
        }
        if(currentState.selectedCategory == "Select Category"){
            newCategoryError = "Please select a category"
            isValid = false
        }

        //ensure qty > 0 and in numeric
        val qtyInt = currentState.quantity.toIntOrNull()
        if(currentState.quantity.isBlank()){
            newQtyError = "Quantity is required"
            isValid = false
        } else if (qtyInt == null || qtyInt < 1) {
            newQtyError = "Quantity must be at least 1"
            isValid = false
        } else if (qtyInt > 100) {
            newQtyError = "Quantity cannot exceed 100"
            isValid = false
        }

        val originalPrice = currentState.originalPrice.toDoubleOrNull()
        if(currentState.originalPrice.isBlank()){
            newPriceError = "Please enter a price"
            isValid = false
        }else if(originalPrice == null || originalPrice <= 0){
            newPriceError = "Please enter a valid price greater than 0"
            isValid = false
        } else if (originalPrice > 100.0) {
            newPriceError = "Price cannot exceed RM 100"
            isValid = false
        }

        val pickupValidationMessage = validatePickupTime(currentState.pickupTime)

        if (currentState.pickupTime.isBlank()) {

            newPickupTimeError = "Please select a pickup time"
            isValid = false

        } else if (pickupValidationMessage != null) {

            newPickupTimeError = pickupValidationMessage
            isValid = false
        }

        //Update all error state in once
        _uiState.update{
            it.copy(
                nameError = newNameError,
                categoryError = newCategoryError,
                qtyError = newQtyError,
                priceError = newPriceError,
                pickupTimeError = newPickupTimeError
            )
        }

        //if pass all the validation --> store data
        if(isValid){
            _uiState.update { it.copy(showConfirmDialog = true) }
        }else{
            _uiEvent.trySend(AddFoodEvent.ShowToast("Please check the input fields"))
        }
    }

    private fun calculatePickupDate(
        pickupTime: String,
        now: LocalDateTime = LocalDateTime.now()
    ): String? {

        return try {
            val parts = pickupTime.split(" - ")

            if (parts.size != 2) {
                return null
            }

            now.toLocalDate().toString()

        } catch (e: Exception) {
            null
        }
    }
    private fun validatePickupTime(
        pickupTime: String
    ): String? {
        return try {
            val parts = pickupTime.split(" - ")

            if (parts.size != 2) {
                return "Please select both start and end time"
            }

            val formatter = DateTimeFormatter.ofPattern(
                "hh:mm a",
                Locale.ENGLISH
            )

            val start = LocalTime.parse(
                parts[0].trim(),
                formatter
            )

            val end = LocalTime.parse(
                parts[1].trim(),
                formatter
            )

            val now = LocalDateTime.now()
            val today = LocalDate.now()

            // --------------------------------
            // 1. Pickup starts today
            // --------------------------------
            val startDate = today

            val startDateTime = LocalDateTime.of(
                startDate,
                start
            )

            // --------------------------------
            // 2. Determine end date
            // --------------------------------
            val endDate =
                if (end.isBefore(start)) {
                    // Example:
                    // 11:00 PM - 1:00 AM
                    // End time is on the next day.
                    startDate.plusDays(1)
                } else {
                    startDate
                }

            val endDateTime = LocalDateTime.of(
                endDate,
                end
            )

            // --------------------------------
            // 3. Start must be in the future
            // --------------------------------
            if (!startDateTime.isAfter(now)) {
                return "Start time must be in the future"
            }

            // --------------------------------
            // 4. Calculate duration
            // --------------------------------
            val durationMinutes =
                Duration.between(
                    startDateTime,
                    endDateTime
                ).toMinutes()

            // --------------------------------
            // 5. Maximum 24 hours
            // --------------------------------
            if (durationMinutes > 24 * 60) {
                return "Pickup time range cannot exceed 24 hours"
            }

            // --------------------------------
            // 6. Minimum 30 minutes
            // --------------------------------
            if (durationMinutes < 30) {
                return "Pickup time must be at least 30 minutes"
            }

            null

        } catch (e: Exception) {
            "Invalid pickup time"
        }
    }

    fun onImageSelected(uri: Uri) {
        _uiState.update {
            it.copy(
                imageUri = uri.toString(),
                imageError = null
            )
        }
    }

    fun dismissDialog(){
        _uiState.update { it.copy(showConfirmDialog = false) }
    }

    fun confirmPublish(
        imageBytes: ByteArray? = null
    ) {
        dismissDialog()

        val currentState = _uiState.value

        val qtyInt = currentState.quantity.toIntOrNull()
        val originalPrice = currentState.originalPrice.toDoubleOrNull()
        val discount = currentState.selectedDiscount.toIntOrNull() ?: 0

        val foodId = editingFoodId ?: UUID.randomUUID().toString()

        // Validate quantity first
        if (qtyInt == null || qtyInt !in 1..100) {
            _uiEvent.trySend(
                AddFoodEvent.ShowToast(
                    "Quantity must be between 1 and 100"
                )
            )
            return
        }

        // Validate price
        if (originalPrice == null || originalPrice !in 0.01..100.0) {
            _uiEvent.trySend(
                AddFoodEvent.ShowToast(
                    "Price must be between RM0.01 and RM100.00"
                )
            )
            return
        }

        // Validate discount
        if (discount !in 0..100) {
            _uiEvent.trySend(
                AddFoodEvent.ShowToast(
                    "Discount must be between 0% and 100%"
                )
            )
            return
        }

        // Validate pickup time
        val pickupError = validatePickupTime(
            currentState.pickupTime
        )

        if (pickupError != null) {
            _uiEvent.trySend(
                AddFoodEvent.ShowToast(pickupError)
            )
            return
        }

        // The system automatically determines the pickup date
        // for both new food and edited food.
        val pickupDate =
            calculatePickupDate(
                currentState.pickupTime
            ) ?: run {
                _uiEvent.trySend(
                    AddFoodEvent.ShowToast(
                        "Invalid pickup date"
                    )
                )
                return
            }

        // Everything that calls suspend functions goes inside here
        viewModelScope.launch {
            try {

                val result = withContext(Dispatchers.IO){
                    // Upload image inside coroutine because uploadFoodImage() is suspend
                    val imageUrl =
                        if (imageBytes != null) {
                            repository.uploadFoodImage(
                                providerId = currentProviderId,
                                foodId = foodId,
                                imageBytes = imageBytes
                            )
                        } else {
                            currentState.imageUrl
                        }

                    val restaurantId = repository.getRestaurantIdByProvider(
                        currentProviderId
                    )

                    if (restaurantId == null) {
                        _uiEvent.trySend(
                            AddFoodEvent.ShowToast(
                                "No restaurant found for this provider"
                            )
                        )
                    }

                    // Create FoodListing
                    val food = FoodListing(
                        id = foodId,
                        providerId = currentProviderId,
                        restaurant = restaurantId,
                        name = currentState.foodName.trim(),
                        category = currentState.selectedCategory,
                        description = currentState.description
                            .trim()
                            .ifBlank { null },
                        quantity = qtyInt,
                        pickupDate = pickupDate,
                        pickupTime = currentState.pickupTime,
                        originalPrice = originalPrice,
                        discountPercentage = discount,
                        imageUrl = imageUrl,
                        price = FoodListing.calculateFinalPrice(
                            originalPrice,
                            discount
                        )
                    )

                    // Save to Supabase
                    repository.upsertFoodListing(food)
                }

                val message = if (editingFoodId == null) {
                    "Food published successfully!"
                } else {
                    "Food updated successfully!"
                }

                _uiEvent.trySend(
                    AddFoodEvent.ShowToast(message)
                )

                _uiEvent.trySend(
                    AddFoodEvent.NavigateToProviderHome
                )

            } catch (e: Exception) {

                _uiEvent.trySend(
                    AddFoodEvent.ShowToast(
                        e.message ?: "Failed to save food"
                    )
                )
            }
        }
    }
}