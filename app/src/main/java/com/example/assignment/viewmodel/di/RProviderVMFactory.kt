package com.example.assignment.viewmodel.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.assignment.viewmodel.RegisterProviderViewModel

class RProviderVMFactory (private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterProviderViewModel::class.java)) {
            return RegisterProviderViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}