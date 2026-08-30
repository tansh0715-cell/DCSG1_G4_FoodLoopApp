package com.example.assignment.screen.payment

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PaymentMethodChip(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) {
        Color(0xFFE8F5E9)
    } else {
        Color.Transparent
    }
    val borderColor = if (isSelected) {
        Color(0xFF2E7D32)
    } else {
        Color(0xFFDCE4EE)
    }

    Box(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(16.dp))
            .border(2.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color(0xFF0B1A33) else Color(0xFF4A5A72)
        )
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .background(Color(0xFFFFCC00), RoundedCornerShape(50))
                    .align(Alignment.TopEnd)
                    .offset(x = 8.dp, y = (-8).dp),
                contentAlignment = Alignment.Center
            ) {
                Text("✓", fontSize = 12.sp, color = Color(0xFF0B1A33))
            }
        }
    }
}