package com.example.assignment.model

import kotlinx.serialization.Serializable

@Serializable
data class Achievement(
    val title: String,
    val description: String,
    val icon: String,
    val quote: String,
    val current:Int,
    val target: Int
    // String replace with painter soon
)