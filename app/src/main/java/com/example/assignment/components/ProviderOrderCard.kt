package com.example.assignment.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment.model.Order

@Composable
fun ProviderOrderCard(
    order: Order,
    onMarkDone: () -> Unit
) {
    val isCompleted = order.status.equals(
        "completed",
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

            Text(
                text = "Order",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onPrimary
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Order ID: ${order.id}",
                fontSize = 13.sp
            )

            Text(
                text = "Pickup Code: ${order.pickupCode}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Quantity: ${order.quantity}",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSecondary
                )

                Text(
                    text = "RM %.2f".format(order.totalPrice),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "Pickup: ${order.pickupTime}",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSecondary
            )

            Text(
                text = "Status: ${order.status}",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSecondary
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

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
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
