package com.example.assignment.model

data class User(
    val id: String = "",
    val email: String,
    val fullName: String? = null,
    val phone: String? = null,
    val accountType: AccountType,
    val restaurantName: String? = null,
    val address: String? = null,
    val licenseUri: String? = null
)
