package com.example.assignment.screen.inventoryModule

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.assignment.ui.theme.ErrorColor
import com.example.assignment.ui.theme.PrimaryGreen
import com.example.assignment.ui.theme.SafeColor
import com.example.assignment.ui.theme.SoonColor
import com.example.assignment.ui.theme.textErrorColor
import com.example.assignment.ui.theme.textSoonColor

@Composable
fun StatusBadge(status: String){ //for inventory
    val (text,textColor,bgColor) = when (status) {
        "SAFE" -> Triple("Safe",PrimaryGreen, SafeColor)
        "EXPIRING_SOON" -> Triple("Expiring Soon", textSoonColor, SoonColor)
        "EXPIRED" -> Triple("Expired", textErrorColor, ErrorColor)
        else -> Triple("Unknown", textErrorColor, ErrorColor)
    }
    Surface(shape = RoundedCornerShape(50.dp), color = bgColor, modifier = Modifier.padding(15.dp)) {Text(text, modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp), color = textColor, style = MaterialTheme.typography.labelMedium) }
}