package com.example.assignment.data.repository

import com.example.assignment.model.ProviderProfileImage
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns

class ProviderProfileRepository(
    private val supabase: SupabaseClient
) {

    suspend fun getProviderProfileImage(
        providerId: String
    ): String? {

        return supabase
            .from("food_providers")
            .select(
                columns = Columns.raw("profile_image_url")
            ) {
                filter {
                    eq("user_id", providerId)
                }
            }
            .decodeSingleOrNull<ProviderProfileImage>()
            ?.profileImageUrl
    }
}