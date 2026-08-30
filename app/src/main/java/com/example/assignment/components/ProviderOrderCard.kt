package com.example.assignment.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.assignment.model.FoodListing
import com.example.assignment.model.Order

@Composable
fun ProviderOrderCard(
    order: Order,
    food: FoodListing?,
    onMarkDone: () -> Unit
) {

    val isCompleted = order.status.equals(
        "COMPLETED",
        ignoreCase = true
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            // =========================
            // Food image + food name
            // =========================
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                AsyncImage(
                    model = food?.imageUrl,
                    contentDescription = food?.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(
                            RoundedCornerShape(12.dp)
                        )
                )

                Spacer(
                    modifier = Modifier.width(16.dp)
                )

                Column {

                    Text(
                        text = food?.name ?: "Food",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = "Order ID: ${order.orderCode}",
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // =========================
            // Quantity + Price
            // =========================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text = "Quantity: ${order.quantity}",
                    fontSize = 13.sp,
                    color =
                        MaterialTheme.colorScheme.onSecondary
                )

                Text(
                    text = "RM %.2f".format(
                        order.totalPrice
                    ),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color =
                        MaterialTheme.colorScheme.primary
                )
            }

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            // =========================
            // Pickup
            // =========================
            Text(
                text = "Pickup: ${order.pickupTime}",
                fontSize = 13.sp,
                color =
                    MaterialTheme.colorScheme.onSecondary
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            // =========================
            // Status
            // =========================
            Text(
                text = "Status: ${order.status}",
                fontSize = 13.sp,
                color =
                    MaterialTheme.colorScheme.onSecondary
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // =========================
            // Mark as Done
            // =========================
            Button(
                onClick = onMarkDone,
                enabled = !isCompleted,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor =
                        if (isCompleted)
                            MaterialTheme.colorScheme.background
                        else
                            MaterialTheme.colorScheme.primary
                )
            ) {

                Text(
                    text =
                        if (isCompleted)
                            "Completed"
                        else
                            "Mark as Done",
                    fontWeight = FontWeight.Bold,
                    color =
                        MaterialTheme.colorScheme.surface
                )
            }
        }
    }
}