package com.example.assignment.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
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
            .clickable(enabled = !isSoldOut || isProvider) {
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
                    if (!food.imageUrl.isNullOrBlank()) {

                        AsyncImage(
                            model = food.imageUrl,
                            contentDescription = food.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                    } else {

                        Text(
                            text = "Food",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 12.sp
                        )
                    }
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
fun ProviderFoodCard(
    food: FoodListing,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {

    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    // Status
    val isSoldOut = food.status == FoodStatus.SOLD_OUT
    val isPickupEnded = food.isPickupTimeEnded()
    val canManage = isSoldOut || isPickupEnded

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(1.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Column(modifier = Modifier.padding(12.dp)){
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)) {
                Box {
                    AsyncImage(
                        model = food.imageUrl,
                        contentDescription = food.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    if (isSoldOut) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.55f))
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = food.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color =
                                if (isSoldOut)
                                    MaterialTheme.colorScheme.onSecondary
                                else
                                    MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        StatusBadge(status = food.status)
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    val infoColor =
                        if (isSoldOut) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.onSecondary
                    Text(
                        text = "Available: ${food.quantity} left",
                        fontSize = 12.sp,
                        color = infoColor
                    )
                    Text(text = "Pickup: ${food.pickupTime}", fontSize = 12.sp, color = infoColor)

                    Spacer(modifier = Modifier.height(6.dp))
                    val priceColor =
                        if (isSoldOut) Color.Gray else MaterialTheme.colorScheme.primary
                    Text(
                        text = "RM${"%.0f".format(food.price)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = priceColor
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFFF1F5F9))) // Divider
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color =
                        if (canManage)
                            MaterialTheme.colorScheme.primaryContainer.copy(
                                alpha = 0.5f
                            )
                        else
                            MaterialTheme.colorScheme.onTertiary.copy(
                                alpha = 1f
                            ),
                    modifier = Modifier.clickable(enabled = canManage) {
                        onEdit()
                    }
                ) {
                    Text(text = "Edit",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color =
                            if (canManage)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSecondary.copy(
                                alpha = 0.65f
                            ),
                        modifier = Modifier.padding(horizontal = 16.dp,
                            vertical = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color =
                        if (canManage)
                            MaterialTheme.colorScheme.errorContainer.copy(
                                alpha = 0.5f
                            )
                        else
                            MaterialTheme.colorScheme.onTertiary.copy(
                                alpha = 1f
                            ),
                    modifier = Modifier.clickable(enabled = canManage) { showDeleteDialog = true }
                ) {
                    Text(text = "Delete",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color =
                            if (canManage)
                                MaterialTheme.colorScheme.error
                            else
                                MaterialTheme.colorScheme.onSecondary.copy(
                                    alpha = 0.65f
                                ),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
            },
            title = {
                Text("Delete Listing")
            },
            text = {
                Text(
                    "Are you sure you want to delete ${food.name}?"
                )
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    }
                ) {
                    Text(
                        text = "Delete",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },

            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun StatusBadge(status: FoodStatus) {

    val badgeColor = when (status) {
        FoodStatus.SOLD_OUT -> Color(0xFFF1F5F9)

        FoodStatus.ALMOST ->
            MaterialTheme.colorScheme.surfaceVariant

        FoodStatus.AVAILABLE ->
            MaterialTheme.colorScheme.primaryContainer
    }

    val badgeTextColor = when (status) {
        FoodStatus.SOLD_OUT -> Color.Gray

        FoodStatus.ALMOST ->
            MaterialTheme.colorScheme.tertiary

        FoodStatus.AVAILABLE ->
            MaterialTheme.colorScheme.primary
    }

    val badgeText = when (status) {
        FoodStatus.SOLD_OUT -> "Sold Out"
        FoodStatus.ALMOST -> "Almost"
        FoodStatus.AVAILABLE -> "Available"
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = badgeColor
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(
                horizontal = 8.dp,
                vertical = 4.dp
            )
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(badgeTextColor)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = badgeText,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = badgeTextColor
            )
        }
    }
}