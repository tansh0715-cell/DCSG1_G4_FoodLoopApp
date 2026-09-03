package com.example.assignment.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import com.example.assignment.model.Restaurant

@Composable
fun SaverOrderCard(
    order: Order,
    food: FoodListing?,
    restaurant: Restaurant?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(16.dp)
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

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
                        text = food?.name ?: "Reservation",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Text(
                        text = restaurant?.name ?: "Restaurant",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSecondary
                    )

                    Text(
                        text = "Pickup: ${order.pickupTime}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = "Quantity: ${order.quantity}", fontSize = 12.sp
                )

                Text(
                    text = "RM ${"%.2f".format(order.totalPrice)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Button(
                onClick = onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "View Details",
                    color = MaterialTheme.colorScheme.surface
                )
            }
        }
    }
}