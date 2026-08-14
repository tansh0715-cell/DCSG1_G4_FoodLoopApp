package com.example.assignment.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val email: String,
    val name: String,
    val password: String,
    val phone: String,
    val type: String,
    val restaurant: String? = null,
    val address: String? = null,
    val licenseUri: String? = null
)
