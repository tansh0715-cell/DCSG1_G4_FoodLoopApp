package com.example.assignment.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProviderProfileImage(
    @SerialName("profile_image_url")
    val profileImageUrl: String? = null
)