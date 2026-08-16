package com.example.assignment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.data.repository.FoodRepository
import com.example.assignment.model.FoodListing
import com.example.assignment.state.AddFoodEvent
import com.example.assignment.state.AddFoodUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

class AddFoodViewModel(
    private val repository: FoodRepository,
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
    fun onQtyChange(newValue: String){
        //keep quantity numeric
        val numericValue = newValue.filter { it.isDigit() }
        _uiState.update {
            it.copy(
                quantity = newValue,
                qtyError = null
            )
        }
    }
    fun onDescriptionChange(newValue: String){
        _uiState.update {
            it.copy(
                description = newValue
            )
        }
    }
    fun onPriceChange(newValue: String){
        _uiState.update {
            it.copy(
                originalPrice = newValue,
                priceError = null
            )
        }
    }
    fun onDiscountChange(newValue: String){
        val numericValue = newValue
            .filter{it.isDigit()}
            .toIntOrNull()?:0

        val finalDiscount = numericValue.coerceIn(0,100)
        val discountText =
            if(newValue.isEmpty())
                ""
            else
                finalDiscount.toString()
        _uiState.update {
            it.copy(selectedDiscount = discountText)
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
    //the same food ID is preserved so the later upsert updates instead of inserting another card
    fun loadFoodForEdit(foodId: String){
        editingFoodId = foodId
        viewModelScope.launch {
            try {
                val food = repository.getFoodListingById(
                    id = foodId,
                    providerId = currentProviderId
                )
                if(food!=null){
                    _uiState.update {
                        it.copy(
                            foodName = food.name,
                            description = food.description ?: "",
                            selectedCategory = food.category,
                            quantity = food.quantity.toString(),
                            pickupTime = food.pickupTime,
                            originalPrice =food.originalPrice.toString(),
                            selectedDiscount = food.discountPercentage.toString()
                        )
                    }
                } else{
                    _uiEvent.trySend(AddFoodEvent.ShowToast("Food listing not found"))
                    return@launch
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
        }else if(qtyInt == null || qtyInt < 0){
            newQtyError = "Quantity cannot be negative"
            isValid = false
        }

        val originalPrice = currentState.originalPrice.toDoubleOrNull()
        if(currentState.originalPrice.isBlank()){
            newPriceError = "Please enter a price"
            isValid = false
        }else if(originalPrice == null || originalPrice <= 0){
            newPriceError = "Please enter a valid price greater than 0"
            isValid = false
        }

        if(currentState.pickupTime.isBlank()){
            newPickupTimeError = "Please select a pickup time"
            isValid = false
        } else if(!isValidPickupTime(currentState.pickupTime)){
            newPickupTimeError = "Pickup time must be valid and in the future"
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

    private fun isValidPickupTime(
        pickupTime: String
    ): Boolean{
        return try {
            val parts = pickupTime.split(" - ")

            if(parts.size != 2){
                return false
            }

            val formatter = DateTimeFormatter
                .ofPattern("hh:mm a", Locale.ENGLISH)
            val start = LocalTime.parse(parts[0], formatter)
            val end = LocalTime.parse(parts[1], formatter)
            val now = LocalTime.now()

            //end must be after start
            if(!end.isAfter(start)){
                return false
            }

            if (!start.isAfter(now)) return false

            true
        } catch (e: Exception){
            false
        }
    }

    fun dismissDialog(){
        _uiState.update { it.copy(showConfirmDialog = false) }
    }

    fun confirmPublish(){
        dismissDialog()

        val currentState = _uiState.value
        val qtyInt = currentState.quantity.toIntOrNull()
        val originalPrice = currentState.originalPrice.toDoubleOrNull()
        val discount = currentState.selectedDiscount.toIntOrNull() ?: 0
        val finalPrice = originalPrice!! * (1-discount/100.0)

        if(
            qtyInt == null ||
            originalPrice == null ||
            currentState.selectedCategory.isBlank() ||
            currentState.pickupTime.isBlank()
        ){
            _uiEvent.trySend(
                AddFoodEvent.ShowToast(
                    "Invalid food information"
                )
            )
            return
        }

        viewModelScope.launch {
            try {

                //create food listing object
                val foodToSave = FoodListing(
                    id = editingFoodId ?: UUID.randomUUID().toString(),
                    providerId = currentProviderId,
                    name = currentState.foodName.trim(),
                    description = currentState.description.trim().ifBlank { null },
                    category = currentState.selectedCategory,
                    quantity = qtyInt,
                    pickupTime = currentState.pickupTime,
                    price = finalPrice,
                    originalPrice = originalPrice,
                    discountPercentage = discount
                )

                //save to supabase
                repository.upsertFoodListing(foodToSave)

                //show diff success message for create and edit
                val message = if (editingFoodId == null) {
                    "Food published successfully!"
                } else {
                    "Food updated successfully!"
                }

                _uiEvent.trySend(AddFoodEvent.ShowToast(message))
                _uiEvent.trySend(AddFoodEvent.NavigateBack)

            } catch (e: Exception){
                //if supabase fails, show error
                _uiEvent.trySend(
                    AddFoodEvent.ShowToast(
                        e.message ?: "Failed to save food"
                    )
                )
            }
        }
    }
}