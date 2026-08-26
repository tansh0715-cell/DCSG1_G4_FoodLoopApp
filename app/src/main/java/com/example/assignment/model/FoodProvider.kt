package com.example.assignment.model


import kotlinx.serialization.Serializable

@Serializable
data class FoodProvider (
    val user_id: String,
    val restaurantName: String,
    val email: String,
    val phone: String,
    val address: String,
    val licensePhotoUri: String,
)