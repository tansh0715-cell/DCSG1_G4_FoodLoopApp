package com.example.assignment.model

import androidx.compose.ui.graphics.Color
import com.example.assignment.ui.theme.PrimaryGreen

data class HomeFoodItem(
    val imageResId: Int,
    val title: String,
    val description: String,
    val oriPrice: Double,
    val quantity: Int,
    val timeLabel: String,
    val discountPercentage: Int
) {
    fun getFinalPrice(): Double {
        return oriPrice * (1.0 - (discountPercentage / 100.0))
    }
    fun getBadgeColor(): Color {
        return when {
            discountPercentage >= 70 -> Color(0xFFF44336)
            discountPercentage >= 50 -> Color(0xFFFF9800)
            else -> PrimaryGreen
        }
    }
}
