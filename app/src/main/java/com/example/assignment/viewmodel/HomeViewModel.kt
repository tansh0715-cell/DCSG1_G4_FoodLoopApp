package com.example.assignment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.data.foodList
import com.example.assignment.model.HomeFoodItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val foods: List<HomeFoodItem> = emptyList(),
    val selectedCategoryIndex: Int = 0,
    val categories: List<String> = listOf("All", "Meals", "Bakery", "Snacks")
)
class HomeViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

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
        viewModelScope.launch{
            _uiState.update {it.copy(isLoading = true)}
            delay(1500)

            allFoods = foodList

            //data received --> stop loading
            _uiState.update {
                it.copy(
                    isLoading = false,
                    foods = allFoods
                )
            }
        }
    }

    fun onCategorySelected(index: Int){
        _uiState.update { it.copy(selectedCategoryIndex = index) }

        //filter
        val currentCategories = _uiState.value.categories
        val selectedCategoryName = currentCategories[index]

        val filteredList = if(index == 0){
            allFoods
        }else{
            //TODO: for future filter data feature based on database
            allFoods.take(1)
        }

        _uiState.update { it.copy(foods=filteredList) }
    }

}