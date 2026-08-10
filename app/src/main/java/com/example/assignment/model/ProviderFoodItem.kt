package com.example.assignment.model

data class ProviderFoodItem(
    val imageResId: Int,
    val title: String,
    val availableCount: Int,
    val pickupTime: String,
    val price: Double
)