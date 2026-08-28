package com.example.assignment.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val id: String,

    @SerialName("consumer_id")
    val consumerId: String,

    @SerialName("provider_id")
    val providerId: String,

    @SerialName("food_id")
    val foodId: String,

    @SerialName("restaurant_id")
    val restaurantId: String,

    val quantity: Int,

    @SerialName("total_price")
    val totalPrice: Double,

    @SerialName("pickup_time")
    val pickupTime: String,

    @SerialName("pickup_code")
    val pickupCode: String,

    @SerialName("payment_success")
    val paymentSuccess: Boolean,

    val status: String,

    @SerialName("created_at")
    val createdAt: String,

    @SerialName("completed_at")
    val completedAt: String? = null
)