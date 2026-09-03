package com.example.assignment.data.repository

import com.example.assignment.model.Order
import com.example.assignment.model.achievementModule.Achievement
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest

class AchievementRepository(
    private val supabase: SupabaseClient
) {

    suspend fun getAchievements(): List<Achievement> {

        return supabase
            .postgrest["achievements"]
            .select()
            .decodeList<Achievement>()
    }


    suspend fun getTotalMealsSaved(
        userId: String
    ): Int {

        val orders =
            supabase
                .postgrest["orders"]
                .select {
                    filter {
                        eq(
                            "consumer_id",
                            userId
                        )
                    }
                }
                .decodeList<Order>()

        return orders.sumOf {
            it.quantity
        }
    }
}

