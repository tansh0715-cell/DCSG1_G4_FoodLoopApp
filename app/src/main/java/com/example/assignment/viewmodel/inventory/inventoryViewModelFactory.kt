package com.example.assignment.viewmodel.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.assignment.data.repository.InventoryRepository

class InventoryViewModelFactory(
    private val saverId: String,
    private val inventoryRepository: InventoryRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(
                InventoryViewModel::class.java
            )
        ) {
            return InventoryViewModel(saverId,inventoryRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel")
    }
}
