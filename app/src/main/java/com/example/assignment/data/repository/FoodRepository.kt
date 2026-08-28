package com.example.assignment.data.repository

import com.example.assignment.model.FoodListing
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.Serializable

@Serializable
data class RestaurantIdRow(
    val id: String
)

//handle fetching food listing, searching for specific food listing, creating & updating food listing
class FoodRepository(private val supabase: SupabaseClient) {
    //fetch all for SAVER (select)
    suspend fun getAllFoodListings(): List<FoodListing>{
        return supabase.postgrest["food_listings"]
            .select()
            .decodeList<FoodListing>()
    }

    //fetch specific to PROVIDER (select + filter)
    suspend fun getFoodListingByProvider(providerId: String): List<FoodListing>{
        return supabase.postgrest["food_listings"]
            .select {
                filter {
                    eq("provider_id", providerId)
                }
            }.decodeList<FoodListing>()
    }

    //fetch single item for edit mode (PROVIDER) (select + filter)
    suspend fun getFoodListingById(
        id: String,
        providerId: String? = null
    ): FoodListing?{
        return supabase.postgrest["food_listings"]
            .select {
                filter {
                    eq("id",id)
                    if(!providerId.isNullOrBlank()){
                        eq("provider_id",providerId)
                    }
                }
            }
            .decodeSingleOrNull<FoodListing>()
    }

    suspend fun deleteFoodListing(
        foodId: String
    ) {
        supabase
            .from("food_listings")
            .delete {
                filter {
                    eq("id", foodId)
                }
            }
    }

    //insert and update
    //if ID does not exist -> insert, already exists -> update (edit food)
    suspend fun upsertFoodListing(food: FoodListing){
        supabase.postgrest["food_listings"].upsert(food)
    }

    suspend fun deleteFoodListing(
        foodId: String,
        providerId: String
    ) {
        supabase.postgrest["food_listings"]
            .delete {
                filter {
                    eq("id", foodId)
                    eq("provider_id", providerId)
                }
            }
    }

    suspend fun uploadFoodImage(
        providerId: String,
        foodId: String,
        imageBytes: ByteArray
    ): String {

        val path = "$providerId/$foodId.jpg"

        val bucket = supabase.storage["food-images"]

        bucket.upload(path, imageBytes){
            upsert = true
        }
        return bucket.publicUrl(path)
    }

    suspend fun getRestaurantIdByProvider(
        providerId: String
    ): String?{
        return supabase.postgrest["restaurants"]
            .select {
                filter {
                    eq("provider_id", providerId)
                }
            }
            .decodeSingleOrNull<RestaurantIdRow>() ?.id
    }
}