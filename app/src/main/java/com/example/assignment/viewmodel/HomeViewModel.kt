package com.example.assignment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.data.foodList
import com.example.assignment.model.HomeFoodItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel: ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    //store all the data fetched from the database without directly exposing it to the UI
    private var allFoods = emptyList<HomeFoodItem>()
    private val _foods = MutableStateFlow<List<HomeFoodItem>>(emptyList())
    val foods: StateFlow<List<HomeFoodItem>> = _foods.asStateFlow() //read-only, UI cannot modify

    private val _selectedCategoryIndex = MutableStateFlow(0) //default as 0 ("All")
    val selectedCategoryIndex: StateFlow<Int> = _selectedCategoryIndex.asStateFlow()

    init {
        loadFoods(); //immediately fetch data once view model created
    }

    private fun loadFoods() {
        viewModelScope.launch {
            _isLoading.value = true
            delay(1500)

            allFoods = foodList
            _foods.value = allFoods

            _isLoading.value = false //data received --> stop loading
        }
    }

    fun onCategorySelected(index: Int){
        _selectedCategoryIndex.value = index
        if(index == 0){
            _foods.value = allFoods
        }else{
            //TODO: for future filter data feature
            _foods.value = allFoods.take(1)
        }
    }

}