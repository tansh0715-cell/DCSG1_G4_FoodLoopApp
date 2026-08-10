package com.example.assignment.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AddFoodViewModel: ViewModel() {
    //record user input
    private val _foodName = MutableStateFlow("")
    val foodName: StateFlow<String> = _foodName.asStateFlow()
    private val _selectedCategory = MutableStateFlow("Select Category")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()
    private val _quantity = MutableStateFlow("")
    val quantity: StateFlow<String> = _quantity.asStateFlow()
    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()
    private val _oriPrice = MutableStateFlow("")
    val oriPrice: StateFlow<String> = _oriPrice.asStateFlow()
    private val _selectedDiscount = MutableStateFlow("")
    val selectedDiscount: StateFlow<String> = _selectedDiscount.asStateFlow()

    //record error info
    private val _nameError = MutableStateFlow<String?>(null)
    val nameError: StateFlow<String?> = _nameError.asStateFlow()
    private val _priceError = MutableStateFlow<String?>(null)
    val priceError: StateFlow<String?> = _priceError.asStateFlow()
    private val _categoryError = MutableStateFlow<String?>(null)
    val categoryError: StateFlow<String?> = _categoryError.asStateFlow()
    private val _qtyError = MutableStateFlow<String?>(null)
    val qtyError: StateFlow<String?> = _qtyError.asStateFlow()

    val categories = listOf("Meals", "Bakery", "Snacks", "Beverages")

    //handle events from the UI
    fun onNameChange(newValue: String){
        _foodName.value = newValue
        _nameError.value = null //if user retry --> clear error msg
    }
    fun onCategoryChange(newCategory: String){
        _selectedCategory.value = newCategory
        _categoryError.value = null
    }
    fun onQtyChange(newValue: String){
        _quantity.value = newValue
        _qtyError.value = null
    }
    fun onDescriptionChange(newValue: String){
        _description.value = newValue
    }
    fun onPriceChange(newValue: String){
        _oriPrice.value = newValue
    }
    fun onDiscountChange(newValue: String){
        val numericValue = newValue.filter{it.isDigit()}.toIntOrNull()?:0
        val finalDiscount = if(numericValue > 100) 100 else numericValue
        _selectedDiscount.value = if(finalDiscount == 0 && newValue.isEmpty()) "" else finalDiscount.toString()
    }

    fun submitFood(): Boolean{
        var isValid = true

        if(_foodName.value.isBlank()){
            _nameError.value = "Please enter a food name"
            isValid = false
        }
        if(_selectedCategory.value == "Select Category"){
            _categoryError.value = "Please select a category"
            isValid = false
        }

        //ensure qty > 0 and in numeric
        val qtyInt = _quantity.value.toIntOrNull()
        if(_quantity.value.isBlank()){
            _qtyError.value = "Quantity is required"
            isValid = false
        }else if(qtyInt == null || qtyInt <= 0){
            _qtyError.value = "Quantity must be greater than 0"
            isValid = false
        }

        val priceDouble = _oriPrice.value.toDoubleOrNull()
        if(_oriPrice.value.isBlank()){
            _priceError.value = "Please enter a price"
            isValid = false
        }else if(priceDouble == null || priceDouble <= 0){
            _priceError.value = "Please enter a valid price greater than 0"
            isValid = false
        }
        return isValid
    }

    //if pass all the validation and confirmed --> store into Supabase
    fun confirmPublish(){
        println("Todo")
        //TODO: Repository
    }
}