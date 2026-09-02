package com.example.assignment.data.repository

import com.example.assignment.model.FoodProvider
import com.example.assignment.model.FoodSaver
import com.example.assignment.model.Restaurant
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage

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

        supabase.from("food_savers")
            .update(
                {
                    set("name", name.trim())
                    set("phone", phone.trim())
                }
            ) {
                filter { eq("user_id", userId) }
            }
    }

    suspend fun updateFoodProvider(
        userId: String,
        restaurantName: String,
        phone: String,
        address: String,
        restaurantPictureUrl: String? = null
    ) {

        supabase.from("food_providers")
            .update(
                {
                    set("restaurantName", restaurantName.trim())
                    set("phone", phone.trim())
                    set("address", address.trim())
                    if (restaurantPictureUrl != null) {
                        set("restaurant_picture", restaurantPictureUrl)
                    }
                }
            ) {
                filter { eq("user_id", userId) }
            }

        supabase.from("restaurants")
            .update(
                {
                    set("name", restaurantName.trim())
                    set("address", address.trim())
                    if (restaurantPictureUrl != null) {
                        set("image_url", restaurantPictureUrl)
                    }
                }
            ) {
                filter { eq("provider_id", userId) }
            }

    }

    suspend fun uploadRestaurantPicture(
        providerId: String,
        imageBytes: ByteArray
    ): String {
        val path = "$providerId/profile_${System.currentTimeMillis()}.jpg"
        val bucket = supabase.storage["restaurant-images"]
        bucket.upload(path, imageBytes) {
            upsert = true
        }
        return bucket.publicUrl(path)
    }
}
