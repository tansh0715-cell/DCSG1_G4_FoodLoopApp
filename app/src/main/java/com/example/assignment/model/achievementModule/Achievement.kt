package com.example.assignment.model.achievementModule

import kotlinx.serialization.Serializable

@Serializable
data class Achievement(
    val title: String,
    val description: String,
    val icon: String,
    val quote: String,
    val target: Int
)