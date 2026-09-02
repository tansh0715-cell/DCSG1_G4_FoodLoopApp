package com.example.assignment.data.repository

import com.example.assignment.data.supabase.supabase
import com.example.assignment.model.FoodProvider
import com.example.assignment.model.FoodSaver
import com.example.assignment.model.Restaurant
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AuthRepository {

    // =========================
    // FOOD SAVER REGISTER
    // =========================
    suspend fun registerFoodSaver(
        name: String,
        email: String,
        phone: String,
        password: String
    ) {
        val cleanEmail = email.trim().lowercase()

        supabase.auth.signUpWith(
            Email,
            redirectUrl = "com.example.assignment://login-callback"
        ) {
            this.email = cleanEmail
            this.password = password

            data = buildJsonObject {
                put("account_type", "FOOD_SAVER")
                put("name", name.trim())
                put("phone", phone.trim())
            }
        }
    }


    // =========================
    // FOOD PROVIDER REGISTER
    // =========================
    suspend fun registerFoodProvider(
        restaurantName: String,
        email: String,
        phone: String,
        address: String,
        licensePhoneUrl: String,
        password: String,
        latitude: Double,
        longitude: Double
    ) {
        val cleanEmail = email.trim().lowercase()

        supabase.auth.signUpWith(
            Email,
            redirectUrl = "com.example.assignment://login-callback"
        ) {
            this.email = cleanEmail
            this.password = password

            data = buildJsonObject {
                put("account_type", "FOOD_PROVIDER")
                put("restaurant_name", restaurantName.trim())
                put("phone", phone.trim())
                put("address", address.trim())
                put("license_photo_uri", licensePhoneUrl)
                put("latitude", latitude)
                put("longitude", longitude)
            }
        }
    }


    // =========================
    // LOGIN
    // =========================
    suspend fun login(
        email: String,
        password: String
    ): String {

        val cleanEmail = email.trim().lowercase()

        try {
            supabase.auth.signInWith(Email) {
                this.email = cleanEmail
                this.password = password
            }
        } catch (e: Exception) {

            if (
                e.message?.contains("email_not_confirmed", ignoreCase = true) == true ||
                e.message?.contains("Email not confirmed", ignoreCase = true) == true
            ) {
                throw Exception("EMAIL_NOT_VERIFIED")
            }

            throw e
        }

        val user = supabase.auth.currentUserOrNull()
            ?: throw Exception("Login failed")

        // Check Email Verification
        if (user.emailConfirmedAt == null) {
            supabase.auth.signOut()
            throw Exception("EMAIL_NOT_VERIFIED")
        }

        // =========================
        // CHECK FOOD SAVER
        // =========================
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

        // =========================
        // CHECK FOOD PROVIDER
        // =========================
        val foodProvider = supabase
            .from("food_providers")
            .select {
                filter {
                    eq("user_id", user.id)
                }
            }
            .decodeSingleOrNull<FoodProvider>()

        if (foodProvider != null) {

            val restaurant = supabase
                .from("restaurants")
                .select {
                    filter {
                        eq("provider_id", user.id)
                    }
                }
                .decodeSingleOrNull<Restaurant>()

            if (restaurant == null) {
                throw Exception(
                    "Restaurant profile is missing. Registration was incomplete."
                )
            }

            return "FOOD_PROVIDER"
        }

        throw Exception("Account profile not found")
    }


    // =========================
    // GET FOOD PROVIDER
    // =========================
    suspend fun getFoodProvider(
        userId: String
    ): FoodProvider? {

        return supabase
            .from("food_providers")
            .select {
                filter {
                    eq("user_id", userId)
                }
            }
            .decodeSingleOrNull<FoodProvider>()
    }


    // =========================
    // FORGOT PASSWORD
    // =========================
    suspend fun resetPassword(
        email: String
    ) {
        val cleanEmail = email.trim().lowercase()

        supabase.auth.resetPasswordForEmail(
            email = cleanEmail,
            redirectUrl = "com.example.assignment://reset-callback"
        )
    }


    // =========================
    // UPDATE PASSWORD
    // =========================
    suspend fun updatePassword(
        newPassword: String
    ) {
        supabase.auth.updateUser {
            password = newPassword
        }
    }


    // =========================
    // LOGOUT
    // =========================
    suspend fun logout() {
        supabase.auth.signOut()
    }
}