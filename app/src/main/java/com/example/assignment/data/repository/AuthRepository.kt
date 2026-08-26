package com.example.assignment.data.repository

import com.example.assignment.data.supabase.supabase
import com.example.assignment.model.FoodProvider
import com.example.assignment.model.FoodSaver
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from

class AuthRepository {
    suspend fun registerFoodSaver(
        name: String,
        email: String,
        phone: String,
        password: String
    ){
        val cleanEmail = email.trim().lowercase()

        supabase.auth.signUpWith(Email){
            this.email = cleanEmail
            this.password = password
        }

        supabase.auth.signInWith(Email) {
            this.email = cleanEmail
            this.password = password
        }

        val user = supabase.auth.currentUserOrNull() ?: throw Exception("User account created but user session is not available.")

        val foodSaver = FoodSaver(
            user_id = user.id,
            name = name.trim(),
            email = cleanEmail,
            phone = phone.trim()
        )

        supabase.from("food_savers").insert(foodSaver)

    }

    suspend fun registerFoodProvider(
        restaurantName: String,
        email: String,
        phone: String,
        address: String,
        licensePhoneUrl: String,
        password: String
    ){

        val cleanEmail = email.trim().lowercase()

        supabase.auth.signUpWith(Email) {
            this.email = cleanEmail
            this.password = password
        }

        supabase.auth.signInWith(Email) {
            this.email = cleanEmail
            this.password = password
        }

        val user = supabase.auth.currentUserOrNull() ?: throw Exception("User account created but user session is not available.")

        val foodProvider = FoodProvider(
            user_id = user.id,
            restaurantName = restaurantName.trim(),
            email = cleanEmail,
            phone = phone.trim(),
            address = address.trim(),
            licensePhotoUri = licensePhoneUrl
        )

        supabase.from("food_providers").insert(foodProvider)

    }

    suspend fun login(
        email: String,
        password: String
    ): String {

        val cleanEmail = email.trim().lowercase()

        supabase.auth.signInWith(Email) {
            this.email = cleanEmail
            this.password = password
        }

        val user = supabase.auth.currentUserOrNull() ?: throw Exception("Login failed")

        val foodSaver = supabase
            .from("food_savers")
            .select {
                filter {
                    eq("user_id", user.id)
                }
            }
            .decodeSingleOrNull<FoodSaver>()

        if (foodSaver != null) {
            return "FOOD_SAVER"
        }

        val foodProvider = supabase
            .from("food_providers")
            .select {
                filter {
                    eq("user_id", user.id)
                }
            }
            .decodeSingleOrNull<FoodProvider>()

        if (foodProvider != null) {
            return "FOOD_PROVIDER"
        }

        throw Exception("Account profile not found")
    }



    suspend fun resetPassword(
        email: String
    ) {
        val cleanEmail = email.trim().lowercase()
        supabase.auth.resetPasswordForEmail(
            email = cleanEmail,
            redirectUrl = "com.example.assignment://login-callback"
        )
    }

    suspend fun updatePassword(
        newPassword: String
    ) {
        supabase.auth.updateUser {
            password = newPassword
        }
    }
}