package com.example.assignment.viewmodel.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class RegisterTypeViewModel : ViewModel() {
    var selectedType by mutableStateOf<String?>(null)
        private set

    fun selectFoodSaver() {
        selectedType = "FOOD_SAVER"
    }

    fun selectFoodProvider() {
        selectedType = "FOOD_PROVIDER"
    }

    fun resetSelection() {
        selectedType = null
    }
}