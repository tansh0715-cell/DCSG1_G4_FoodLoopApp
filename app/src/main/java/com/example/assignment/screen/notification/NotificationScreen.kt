package com.example.assignment.screen.notification

import androidx.compose.foundation.BorderStroke
import com.example.assignment.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment.data.supabase.supabase
import com.example.assignment.notification.NotificationHistoryStore
import com.example.assignment.notification.StoredNotification
import com.example.assignment.ui.theme.BackgroundColor
import com.example.assignment.viewmodel.order.OrderViewModel
import io.github.jan.supabase.auth.auth

@Composable
fun NotificationScreen(
    innerPadding: PaddingValues, orderViewModel: OrderViewModel
) {

    val context = LocalContext.current

    var notifications by remember {
        mutableStateOf(
            emptyList<StoredNotification>()
        )
    }
    val userId =
        supabase.auth.currentUserOrNull()?.id.orEmpty()

    LaunchedEffect(userId) {

        if (userId.isNotEmpty()) {

            notifications =
                NotificationHistoryStore(
                    context
                ).getNotificationsFor(
                    ownerId = userId,
                    role = "FOOD_SAVER"
                )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            )
            .padding(innerPadding)
            .padding(16.dp)
    ) {

        Text(
            text = "Notifications",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary,

            modifier = Modifier.padding(
                bottom = 16.dp
            )
        )

        if (notifications.isEmpty()) {

            Text(
                text = "No notifications yet.",

                color = MaterialTheme.colorScheme.onSecondary
            )

        } else {

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(
                    notifications, key = {
                        it.id
                    }) { notification ->

                    NotificationHistoryCard(
                        notification
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationHistoryCard(
    notification: StoredNotification
) {

    val (iconResId, iconColor) = when (notification.title) {

        "Food Collected" ->
            R.drawable.check_circle_24dp_cccccc_fill0_wght400_grad0_opsz24 to
                    MaterialTheme.colorScheme.primary

        "Pickup Time Passed" ->
            R.drawable.alarm_24dp_2854c5_fill0_wght400_grad0_opsz24 to
                    MaterialTheme.colorScheme.secondaryContainer

        "New Consumer Order" ->
            R.drawable.add_circle_24dp_e3e3e3_fill0_wght400_grad0_opsz24 to
                    MaterialTheme.colorScheme.onSecondary

        "Low Stock Alert" ->
            R.drawable.warning_24dp_f19e39_fill0_wght400_grad0_opsz24 to
                    MaterialTheme.colorScheme.tertiary

        "Food Sold Out" ->
            R.drawable.production_quantity_limits_24dp_cccccc_fill0_wght400_grad0_opsz24 to
                    MaterialTheme.colorScheme.error

        "Order Canceled" ->
            R.drawable.cancel_24dp_cccccc_fill0_wght400_grad0_opsz24 to
                    MaterialTheme.colorScheme.error

        else ->
            R.drawable.ic_notification to
                    MaterialTheme.colorScheme.primary
    }

    Card(
        shape = RoundedCornerShape(12.dp),

        colors = CardDefaults.cardColors(
            containerColor = BackgroundColor
        ),

        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.onSecondary.copy(
                alpha = 0.2f
            )
        ),

        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier.padding(16.dp),

            verticalAlignment =
                androidx.compose.ui.Alignment.CenterVertically
        ) {

            Icon(
                painter = painterResource(
                    id = iconResId
                ),

                contentDescription =
                    notification.title,

                modifier = Modifier.size(32.dp),

                tint = iconColor
            )

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = notification.title,

                    fontSize = 14.sp,

                    fontWeight = FontWeight.Bold,

                    color = MaterialTheme.colorScheme.onPrimary
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = notification.message,

                    fontSize = 13.sp,

                    color = MaterialTheme.colorScheme.onPrimary
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = relativeNotificationTime(
                        notification.timestamp
                    ),

                    fontSize = 12.sp,

                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
    }
}

@Composable
fun ProviderNotificationScreen(
    innerPadding: PaddingValues, orderViewModel: OrderViewModel
) {

    val context = LocalContext.current

    var notifications by remember {
        mutableStateOf(
            emptyList<StoredNotification>()
        )
    }
    val userId = supabase.auth.currentUserOrNull()?.id.orEmpty()

    LaunchedEffect(Unit) {

        notifications = NotificationHistoryStore(
            context
        ).getNotificationsFor(
            ownerId = userId,
            role = "FOOD_PROVIDER"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            )
            .padding(innerPadding)
            .padding(16.dp)
    ) {

        Text(
            text = "Notifications",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary,

            modifier = Modifier.padding(
                bottom = 16.dp
            )
        )

        if (notifications.isEmpty()) {

            Text(
                text = "There is nothing for now.",

                color = MaterialTheme.colorScheme.onSecondary
            )

        } else {

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(
                    notifications, key = {
                        it.id
                    }) { notification ->

                    NotificationHistoryCard(
                        notification
                    )
                }
            }
        }
    }
}

private fun relativeNotificationTime(
    timestamp: Long
): String {

    val now = System.currentTimeMillis()

    val minutes = (now - timestamp).coerceAtLeast(0).div(60_000)

    return when {

        minutes < 1 -> "Just now"

        minutes < 60 -> "$minutes min ago"

        minutes < 1440 -> {

            val hours = minutes / 60

            "$hours hour${
                if (hours == 1L) ""
                else "s"
            } ago"
        }

        else -> {

            val days = minutes / 1440

            "$days day${
                if (days == 1L) ""
                else "s"
            } ago"
        }
    }
}