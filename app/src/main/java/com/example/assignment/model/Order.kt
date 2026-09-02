package com.example.assignment.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Serializable
data class Order(
    val id: String,

    @SerialName("consumer_id") val consumerId: String,

    @SerialName("provider_id") val providerId: String,

    @SerialName("food_id") val foodId: String,

    @SerialName("restaurant_id") val restaurantId: String,

    val quantity: Int,

    @SerialName("total_price") val totalPrice: Double,

    @SerialName("pickup_time") val pickupTime: String,

    @SerialName("pickup_code") val pickupCode: String,

    @SerialName("payment_success") val paymentSuccess: Boolean,

    @SerialName("order_code") val orderCode: String,

    val status: String,

    @SerialName("created_at") val createdAt: String,

    @SerialName("completed_at") val completedAt: String? = null
)

// Extension function for Order
fun Order.isPickupTimeEnded(): Boolean {

    if (pickupTime.isBlank()) {
        return false
    }

    return try {

        val parts = pickupTime.split(" - ")

        if (parts.size != 2) {
            return false
        }

        val formatter = DateTimeFormatter.ofPattern(
            "h:mm a", Locale.ENGLISH
        )

        val startTime = LocalTime.parse(
            parts[0].trim(), formatter
        )

        val endTime = LocalTime.parse(
            parts[1].trim(), formatter
        )

        val today = LocalDate.now()

        var endDate = today

        /*
         * If end time is earlier than start time,
         * the pickup slot crosses midnight.
         * End time belongs to the next day.
         */

        if (endTime.isBefore(startTime)) {
            endDate = today.plusDays(1)
        }

        val pickupEndDateTime = LocalDateTime.of(
            endDate, endTime
        )

        val now = LocalDateTime.now()
        now.isAfter(pickupEndDateTime)

    } catch (e: Exception) {

        e.printStackTrace()

        false
    }
}