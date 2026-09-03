package com.example.assignment.model

import androidx.compose.ui.graphics.Color
import com.example.assignment.ui.theme.PrimaryGreen
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

enum class FoodStatus{
    AVAILABLE, ALMOST, SOLD_OUT
}

@Serializable
data class FoodListing(
    val id: String = UUID.randomUUID().toString(),

    @SerialName("provider_id")
    val providerId: String,

    @SerialName("restaurant_id")
    val restaurant: String?,

    //Food information
    val name: String,
    val category: String,
    val description: String? = null,
    val quantity: Int,

    @SerialName("pickup_time")
    val pickupTime: String,
    val price: Double, //final selling price after discount

    @SerialName("pickup_date")
    val pickupDate: String,

    @SerialName("original_price")
    val originalPrice: Double,

    @SerialName("discount_percentage")
    val discountPercentage: Int,

    @SerialName("image_url")
    val imageUrl: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null
){
    //status always derived from the current quantity
    val status: FoodStatus get() = getFoodStatus(quantity)

    //Return true when the pickup time range has already ended
    fun isPickupTimeEnded(): Boolean {

        if (pickupDate.isBlank() || pickupTime.isBlank()) {
            return false
        }

        return try {

            val parts = pickupTime.split(" - ")

            if (parts.size != 2) {
                return false
            }

            val formatter = DateTimeFormatter.ofPattern(
                "h:mm a",
                Locale.ENGLISH
            )

            val start = LocalTime.parse(
                parts[0].trim(),
                formatter
            )

            val end = LocalTime.parse(
                parts[1].trim(),
                formatter
            )

            val pickupStartDate = LocalDate.parse(
                pickupDate
            )

            val pickupEndDate =
                if (end.isBefore(start)) {
                    pickupStartDate.plusDays(1)
                } else {
                    pickupStartDate
                }

            val pickupEndDateTime = LocalDateTime.of(
                pickupEndDate,
                end
            )

            LocalDateTime.now()
                .isAfter(pickupEndDateTime)

        } catch (e: Exception) {
            false
        }
    }

    companion object{
        fun getFoodStatus(
            quantity: Int,
            almostThreshold: Int = 5
        ): FoodStatus{
            return when{
                quantity <= 0 -> FoodStatus.SOLD_OUT
                quantity <= almostThreshold -> FoodStatus.ALMOST
                else -> FoodStatus.AVAILABLE
            }
        }

        fun calculateFinalPrice(
            originalPrice: Double,
            discountPercentage: Int
        ): Double {
            return originalPrice *
                    (1 - discountPercentage.coerceIn(0, 100) / 100.0)
        }
    }
}

fun FoodListing.getDiscountBadgeColor(): Color {
    return when {
        discountPercentage >= 70 ->
            Color(0xFFF44336)

        discountPercentage >= 50 ->
            Color(0xFFFF9800)

        else ->
            PrimaryGreen
    }
}