package com.example.assignment.data.repository

import com.example.assignment.model.FoodProvider
import com.example.assignment.model.FoodSaver
import com.example.assignment.model.Restaurant
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

class ProfileRepository(
    private val supabase: SupabaseClient
) {

    suspend fun getFoodSaver(userId: String): FoodSaver? {
        return supabase.from("food_savers")
            .select {
                filter { eq("user_id", userId) }
            }
            .decodeSingleOrNull<FoodSaver>()
    }

    suspend fun getFoodProvider(userId: String): FoodProvider? {
        return supabase.from("food_providers")
            .select {
                filter { eq("user_id", userId) }
            }
            .decodeSingleOrNull<FoodProvider>()
    }

    suspend fun getRestaurantByProvider(providerId: String): Restaurant? {
        return supabase.from("restaurants")
            .select {
                filter { eq("provider_id", providerId) }
            }
            .decodeSingleOrNull<Restaurant>()
    }

    suspend fun updateFoodSaver(
        userId: String,
        name: String,
        phone: String
    ) {
        val existing = getFoodSaver(userId)
            ?: throw Exception("Food saver profile not found")

        val updated = existing.copy(
            name = name.trim(),
            phone = phone.trim()
        )
        supabase.from("food_savers").upsert(updated)
    }

    suspend fun updateFoodProvider(
        userId: String,
        restaurantName: String,
        phone: String,
        address: String
    ) {
        val existing = getFoodProvider(userId)
            ?: throw Exception("Food provider profile not found")

        val updatedProvider = existing.copy(
            restaurantName = restaurantName.trim(),
            phone = phone.trim(),
            address = address.trim()
        )
        supabase.from("food_providers").upsert(updatedProvider)

        // Keep restaurants table in sync (name + address)
        val restaurant = getRestaurantByProvider(userId)
        if (restaurant != null) {
            val updatedRestaurant = restaurant.copy(
                name = restaurantName.trim(),
                address = address.trim()
            )
            supabase.from("restaurants").upsert(updatedRestaurant)
        }
    }
}
