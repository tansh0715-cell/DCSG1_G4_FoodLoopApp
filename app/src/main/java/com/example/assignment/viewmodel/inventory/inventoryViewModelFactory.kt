package com.example.assignment.viewmodel.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class InventoryViewModelFactory(
    private val saverId: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(
                InventoryViewModel::class.java
            )
        ) {
            return InventoryViewModel(saverId) as T
        }

        throw IllegalArgumentException("Unknown ViewModel")
    }
}
