package com.example.assignment.model

data class User(
    val name: String,
    val email: String,
    val password: String,
    val phone: String,
    val type: String,
    val restaurant: String? = null,
    val  address: String? = null
)
