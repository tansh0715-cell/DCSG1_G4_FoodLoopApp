package com.example.assignment.model

data class  Reservation(
    val orderId: String,
    val imageResId: Int,
    val foodName: String,
    val restaurantName: String,
    val pickupTimeRange: String,
    val pickupCountdown: String,
    val price: Double,
    val quantity: Int,
    val address: String,
    val distance: String,
    val code: String,
    var isCompleted: Boolean = false
)
