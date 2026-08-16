package com.example.assignment.model

import android.health.connect.datatypes.units.Percentage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

enum class UserRole{
    PROVIDER, SAVER
}
enum class FoodStatus{
    AVAILABLE, ALMOST, SOLD_OUT
}
enum class FoodCategory{
    Meals,
    Bakery,
    Snacks
}

@Serializable
data class FoodListing(
    val id: String = UUID.randomUUID().toString(),

    @SerialName("provider_id")
    val providerId: String,

    //Food information
    val name: String,
    val category: String,
    val description: String? = null,
    val quantity: Int,

    @SerialName("pickup_time")
    val pickupTime: String,
    val price: Double = 0.0, //final selling price after discount
    val originalPrice: Double,
    val discountPercentage: Int,

    @SerialName("created_at") val createdAt: String? = null
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
    }
}
