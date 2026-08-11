package com.example.assignment.viewmodel

import androidx.lifecycle.ViewModel
import com.example.assignment.state.AddFoodEvent
import com.example.assignment.state.AddFoodUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

class AddFoodViewModel: ViewModel() {
    //record UI state
    private val _uiState = MutableStateFlow(AddFoodUiState())
    val uiState: StateFlow<AddFoodUiState> = _uiState.asStateFlow()

    //prevent duplicate toast
    private val _uiEvent = Channel<AddFoodEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    val categories = listOf("Meals", "Bakery", "Snacks", "Beverages")

    //handle events from the UI
    fun onNameChange(newValue: String){
        //copy existing state & only modify the changed parts
        _uiState.update { currentState ->
            currentState.copy(
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
        val numericValue = newValue.filter{it.isDigit()}.toIntOrNull()?:0
        val finalDiscount = if(numericValue > 100) 100 else numericValue
        val discount = if(finalDiscount == 0 && newValue.isEmpty()) "" else finalDiscount.toString()
        _uiState.update { it.copy(selectedDiscount = discount) }
    }

    fun submitFood(){
        val currentState = _uiState.value
        var isValid = true

        var newNameError: String? = null
        var newCategoryError: String? = null
        var newQtyError: String? = null
        var newPriceError: String? = null

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
        }else if(qtyInt == null || qtyInt <= 0){
            newQtyError = "Quantity must be greater than 0"
            isValid = false
        }

        val priceDouble = currentState.originalPrice.toDoubleOrNull()
        if(currentState.originalPrice.isBlank()){
            newPriceError = "Please enter a price"
            isValid = false
        }else if(priceDouble == null || priceDouble <= 0){
            newPriceError = "Please enter a valid price greater than 0"
            isValid = false
        }

        //Update all error state in once
        _uiState.update{
            it.copy(
                nameError = newNameError,
                categoryError = newCategoryError,
                qtyError = newQtyError,
                priceError = newPriceError
            )
        }

        //if pass all the validation --> store data
        if(isValid){
            _uiState.update { it.copy(showConfirmDialog = true) }
        }else{
            _uiEvent.trySend(AddFoodEvent.ShowToast("Please check the input fields"))
        }
    }

    fun dismissDialog(){
        _uiState.update { it.copy(showConfirmDialog = false) }
    }

    fun confirmPublish(){
        dismissDialog()
        println("Saving to database")
        _uiEvent.trySend(AddFoodEvent.ShowToast("Food published successfully!"))
        _uiEvent.trySend(AddFoodEvent.NavigateBack)
    }
}