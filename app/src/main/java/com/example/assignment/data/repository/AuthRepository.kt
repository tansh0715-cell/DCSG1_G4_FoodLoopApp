package com.example.assignment.data.repository

import com.example.assignment.data.supabase.AuthService
import com.example.assignment.model.AccountType

class AuthRepository(private val authService: AuthService) {

    suspend fun registerSaver(
        email: String,
        password: String,
        fullName: String,
        phone: String
    ): Result<Unit> {
        val userData = mapOf(
            "full_name" to fullName,
            "phone" to phone,
            "account_type" to AccountType.FOOD_SAVER.name
        )
        return authService.signUp(email, password, userData)
    }

    suspend fun registerProvider(
        email: String,
        password: String,
        restaurantName: String,
        phone: String,
        address: String,
        licenseUrl: String? = null
    ): Result<Unit> {
        val userData = mapOf(
            "restaurant_name" to restaurantName,
            "phone" to phone,
            "address" to address,
            "account_type" to AccountType.FOOD_PROVIDER.name,
            "license_url" to (licenseUrl ?: "")
        )
        return authService.signUp(email, password, userData)
    }

    suspend fun login(email: String, password: String): Result<Unit> {
        return authService.signIn(email, password)
    }

    fun getCurrentUser() = authService.getCurrentUser()
}