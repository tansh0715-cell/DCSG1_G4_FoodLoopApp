package com.example.assignment.model

import kotlinx.serialization.Serializable

@Serializable
data class FoodSaver(
    val user_id: String,
    val name: String,
    val email: String,
    val phone: String
)