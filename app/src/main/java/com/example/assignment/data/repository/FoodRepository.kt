package com.example.assignment.data.repository

import com.example.assignment.model.FoodListing
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest

class FoodRepository(private val supabase: SupabaseClient) {
    suspend fun getAllFoodListings(): List<FoodListing>{
        return supabase.postgrest["food_listings"]
            .select()
            .decodeList<FoodListing>()
    }

    suspend fun getFoodListingByProvider(providerId: String): List<FoodListing>{
        return supabase.postgrest["food_listings"]
            .select {
                filter {
                    eq("provider_id", providerId)
                }
            }.decodeList<FoodListing>()
    }

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

    suspend fun upsertFoodListing(food: FoodListing){
        supabase.postgrest["food_listings"].upsert(food)
    }

}