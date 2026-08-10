package com.example.assignment.model

import androidx.compose.ui.graphics.Color

data class NotificationItem(
    val title: String,
    val timeAgo: String,
    val iconResId: Int,
    val containerColor: Color,
    val iconTint: Color
)
