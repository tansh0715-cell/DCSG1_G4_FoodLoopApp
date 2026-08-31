package com.example.assignment.model.inventoryModule

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class Food(
    val item_id: String,
    val saver_id: String,
    val name: String,
    val reminder_days: Int,
    val status: String,
    val expireDate: LocalDate,
    val image_url: String? = null
)

