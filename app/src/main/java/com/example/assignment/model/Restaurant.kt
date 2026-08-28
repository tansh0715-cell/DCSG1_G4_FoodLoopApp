package com.example.assignment.model

import kotlinx.serialization.Serializable

@Serializable
data class Restaurant(
    val id: String,
    val provider_id: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val image_url: String? = null
)