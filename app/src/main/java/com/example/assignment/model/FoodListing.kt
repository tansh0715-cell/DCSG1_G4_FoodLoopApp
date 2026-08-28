package com.example.assignment.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
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