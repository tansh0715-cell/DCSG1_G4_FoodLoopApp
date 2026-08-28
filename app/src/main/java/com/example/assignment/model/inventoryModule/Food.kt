package com.example.assignment.model.inventoryModule

import kotlinx.serialization.Serializable

@Serializable
data class Food(
    val name: String,
    val quantity: String,
    val status: FoodStatus
)

enum class FoodStatus { //for inventory filtering
    SAFE,
    EXPIRING_SOON,
    EXPIRED
}
