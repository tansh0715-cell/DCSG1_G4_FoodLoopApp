package com.example.assignment.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment.model.FoodListing
import com.example.assignment.model.FoodStatus

@Composable
fun FoodCard(
    food: FoodListing,
    isProvider: Boolean,
    onEditClick: () -> Unit,
    onCardClick: () -> Unit = {}
){
    val isSoldOut = food.status == FoodStatus.SOLD_OUT
    val cardAlpha = if (isSoldOut) 0.55f else 1f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(enabled = !isSoldOut || isProvider){
                onCardClick()
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = cardAlpha)
        ),
        elevation = CardDefaults.cardElevation(1.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Food",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp
                    )
                }
            }
            Spacer(modifier = Modifier.size(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = food.name,
                        fontSize = 16.sp,
                        color = if (isSoldOut) MaterialTheme.colorScheme.onSecondary
                        else MaterialTheme.colorScheme.onPrimary,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.size(6.dp))
                    StatusBadge(food.status)
                }

                if (!food.description.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = food.description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSecondary,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Category: ${food.category}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSecondary
                )
                Text(
                    text = "Pickup: ${food.pickupTime}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSecondary
                )
                Text(
                    text = if (isSoldOut) "Sold out" else "Only ${food.quantity} left",
                    fontSize = 12.sp,
                    color = if (isSoldOut) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (food.discountPercentage > 0) {
                Text(
                    text = "RM ${"%.2f".format(food.originalPrice)}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSecondary,
                    textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                )
                Spacer(modifier = Modifier.size(8.dp))
            }

            Text(
                text = "RM ${"%.2f".format(food.price)}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.weight(1f))

            if (isProvider) {
                Button(onClick = onEditClick) {
                    Text("Edit")
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: FoodStatus) {
    val (background, foreground, text) = when (status) {
        FoodStatus.AVAILABLE -> Triple(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.primary,
            "Available"
        )
        FoodStatus.ALMOST -> Triple(
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.tertiary,
            "Almost"
        )
        FoodStatus.SOLD_OUT -> Triple(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant,
            "Sold Out"
        )
    }

    Surface(
        color = background,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(foreground)
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(text = text, color = foreground, fontSize = 11.sp)
        }
    }
}