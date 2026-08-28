package com.example.assignment.data.repository

import com.example.assignment.model.Restaurant
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class NearbyRestaurantRow(
    val id: String,

    @SerialName("provider_id")
    val providerId: String,

    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,

    @SerialName("image_url")
    val imageUrl: String? = null,

    @SerialName("distance_meters")
    val distanceMeters: Double
)

class RestaurantRepository(
    private val supabase: SupabaseClient
) {

    suspend fun getNearbyRestaurants(
        latitude: Double,
        longitude: Double
    ): List<NearbyRestaurantRow> {

        return supabase.postgrest.rpc(
            "nearby_restaurants",
            mapOf(
                "user_lat" to latitude,
                "user_lon" to longitude,
                "radius_meters" to 10000.0
            )
        ).decodeList()
    }

    suspend fun getRestaurantById(
        restaurantId: String
    ): Restaurant? {

        return supabase.postgrest["restaurants"]
            .select {
                filter {
                    eq("id", restaurantId)
                }
            }
            .decodeSingleOrNull<Restaurant>()
    }

    suspend fun getRestaurantByProvider(
        providerId: String
    ): Restaurant? {

        return supabase.postgrest["restaurants"]
            .select {
                filter {
                    eq("provider_id", providerId)
                }
            }
            .decodeSingleOrNull<Restaurant>()
    }
    suspend fun getRestaurantIdByProvider(
        providerId: String
    ): String? {

        return supabase
            .from("restaurants")
            .select {
                filter {
                    eq("provider_id", providerId)
                }
            }
            .decodeSingleOrNull<RestaurantIdRow>()
            ?.id
    }

    suspend fun createRestaurant(
        providerId: String,
        name: String,
        address: String,
        latitude: Double,
        longitude: Double
    ): String {

        val restaurantId = UUID.randomUUID().toString()

        val restaurant = Restaurant(
            id = restaurantId,
            provider_id = providerId,
            name = name,
            address = address,
            latitude = latitude,
            longitude = longitude
        )

        supabase
            .from("restaurants")
            .insert(restaurant)

        return restaurantId
    }

    //avoid duplicate restaurant create
    suspend fun ensureRestaurantForProvider(
        providerId: String,
        name: String,
        address: String,
        latitude: Double,
        longitude: Double
    ): String {

        val existingRestaurant =
            getRestaurantIdByProvider(providerId)

        if (existingRestaurant != null) {

            return existingRestaurant
        }

        return createRestaurant(
            providerId = providerId,
            name = name,
            address = address,
            latitude = latitude,
            longitude = longitude
        )
    }
}