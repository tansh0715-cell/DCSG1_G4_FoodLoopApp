package com.example.assignment.screen.notification

import java.time.LocalTime
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.assignment.R
import com.example.assignment.model.ConsumerNotification
import com.example.assignment.model.FoodListing
import com.example.assignment.model.NotificationItem
import com.example.assignment.model.Order
import com.example.assignment.ui.theme.BackgroundColor
import com.example.assignment.ui.theme.PrimaryBlue
import com.example.assignment.ui.theme.PrimaryGreen
import com.example.assignment.ui.theme.PrimaryYellow
import com.example.assignment.ui.theme.SafeColor
import com.example.assignment.ui.theme.SecondaryBlue
import com.example.assignment.ui.theme.SecondaryYellow
import com.example.assignment.ui.theme.SoonColor
import com.example.assignment.ui.theme.textSoonColor
import com.example.assignment.viewmodel.order.OrderViewModel
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.Instant
import java.time.LocalTime.parse
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun NotificationScreen(
    innerPadding: PaddingValues,
    orderViewModel: OrderViewModel
) {
    val orders by orderViewModel.allNotificationOrders.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        while (true) {

            orderViewModel.loadConsumerNotificationOrders()

            delay(
                30_000
            )
        }
    }

    val notifications =
        rememberConsumerNotifications(
            orders = orders
        )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                text = "You're all caught up.",
                color =
                    MaterialTheme.colorScheme.onSecondary
            )

        } else {

            LazyColumn(
                verticalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                items(
                    notifications,
                    key = { it.id }
                ) { notification ->

                    Card(
                        shape =
                            RoundedCornerShape(12.dp),

                        colors =
                            CardDefaults.cardColors(
                                containerColor =
                                    BackgroundColor
                            ),

                        border =
                            BorderStroke(
                                1.dp,
                                MaterialTheme
                                    .colorScheme
                                    .onSecondary
                                    .copy(alpha = 0.2f)
                            )
                    ) {

                        Row(
                            modifier =
                                Modifier.padding(16.dp),

                            verticalAlignment =
                                Alignment.Top
                        ) {

                            Surface(
                                shape = CircleShape,
                                color =
                                    notification.containerColor,
                                modifier =
                                    Modifier.size(40.dp)
                            ) {

                                Box(
                                    contentAlignment =
                                        Alignment.Center
                                ) {

                                    Icon(
                                        painter =
                                            painterResource(
                                                notification.iconResId
                                            ),

                                        contentDescription =
                                            null,

                                        tint =
                                            notification.iconTint,

                                        modifier =
                                            Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(
                                modifier =
                                    Modifier.width(12.dp)
                            )

                            Column(
                                modifier =
                                    Modifier.weight(1f)
                            ) {

                                Text(
                                    text =
                                        notification.title,

                                    fontSize = 14.sp,

                                    color =
                                        MaterialTheme
                                            .colorScheme
                                            .onPrimary,

                                    lineHeight = 20.sp
                                )

                                Spacer(
                                    modifier =
                                        Modifier.height(4.dp)
                                )

                                Text(
                                    text =
                                        notification.timeAgo,

                                    fontSize = 12.sp,

                                    color =
                                        MaterialTheme
                                            .colorScheme
                                            .onSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProviderNotificationScreen(
    innerPadding: PaddingValues,
    orderViewModel: OrderViewModel
) {
    val orders by orderViewModel.providerNotificationOrders.collectAsStateWithLifecycle()
    val foods by orderViewModel.providerNotificationFoods.collectAsStateWithLifecycle()
    val notifications = rememberProviderNotifications(
        orders = orders,
        foods = foods
    )

    LaunchedEffect(Unit) {
        orderViewModel.loadProviderNotificationOrders()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(innerPadding)
            .padding(16.dp)
    ) {
        Text(
            text = "Notifications",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (notifications.isEmpty()){
            Text(text = "There is nothing for now.")
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(
                notifications,
                key = {it.id}
            ) { notification ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.background)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = notification.containerColor,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    painter = painterResource(notification.iconResId),
                                    contentDescription = null,
                                    tint = notification.iconTint,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = notification.title,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onPrimary,
                                lineHeight = 20.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = notification.timeAgo,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun rememberConsumerNotifications(
    orders: List<Order>
): List<ConsumerNotification> {

    val now = Instant.now()

    val formatter =
       DateTimeFormatter.ofPattern(
            "hh:mm a",
            Locale.ENGLISH
       )

    val localNow =
        LocalTime.now()

    return remember(
        orders,
        localNow.hour,
        localNow.minute
    ) {

        buildList {
            orders.forEach { order ->
                //Reservation confirmed
                if (!order.status.equals(
                        "COMPLETED", ignoreCase = true
                    )
                ) {

                    val createdAt = runCatching {
                        Instant.parse(
                            order.createdAt
                        )
                    }.getOrNull()

                    if (createdAt != null) {

                        val minutesAgo = Duration.between(
                            createdAt, now
                        ).toMinutes()

                        if (minutesAgo in 0..1440) {

                            add(
                                ConsumerNotification(

                                    id = "confirmed-${order.id}",

                                    title = "Reservation confirmed. Order #${order.orderCode} is ready for pickup.",

                                    timeAgo = relativeNotificationTime(
                                        createdAt, now
                                    ),

                                    iconResId = R.drawable.check_circle_24dp_cccccc_fill0_wght400_grad0_opsz24,

                                    containerColor = SafeColor,

                                    iconTint = PrimaryGreen
                                )
                            )
                        }
                    }
                }

                //Completed/Food collected
                if (order.status.equals(
                        "COMPLETED", ignoreCase = true
                    )
                ) {

                    val completedAt = order.completedAt?.let {

                        runCatching {
                            Instant.parse(it)
                        }.getOrNull()
                    }

                    if (completedAt != null) {

                        val minutesAgo = Duration.between(
                            completedAt, now
                        ).toMinutes()

                        if (minutesAgo in 0..1440) {

                            add(
                                ConsumerNotification(

                                    id = "completed-${order.id}",

                                    title = "Your food was successfully collected.",

                                    timeAgo = relativeNotificationTime(
                                        completedAt, now
                                    ),

                                    iconResId = R.drawable.check_circle_24dp_cccccc_fill0_wght400_grad0_opsz24,

                                    containerColor = SafeColor,

                                    iconTint = PrimaryGreen
                                )
                            )
                        }
                    }
                }

                //pickup approaching
                if (!order.status.equals(
                        "COMPLETED", ignoreCase = true
                    )
                ) {

                    val pickupStart = runCatching {

                        parse(
                            order.pickupTime.substringBefore("-").trim(),

                            DateTimeFormatter.ofPattern(
                                "h:mm a", Locale.ENGLISH
                            )
                        )

                    }.getOrNull()


                    if (pickupStart != null) {

                        val minutesUntil = Duration.between(
                            localNow, pickupStart
                        ).toMinutes()


                        if (minutesUntil in 0..60) {

                            add(
                                ConsumerNotification(

                                    id = "pickup-${order.id}",

                                    title = "Your pickup time is approaching. Don't forget to collect your food!",

                                    timeAgo = if (minutesUntil == 0L) {
                                        "Pickup time is now"
                                    } else {
                                        "Pickup starts in $minutesUntil min"
                                    },

                                    iconResId = R.drawable.warning_24dp_f19e39_fill0_wght400_grad0_opsz24,

                                    containerColor = SecondaryYellow,

                                    iconTint = PrimaryYellow
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun relativeNotificationTime(
    instant: Instant,
    now: Instant
): String {

    val minutes =
        Duration
            .between(
                instant,
                now
            )
            .toMinutes()
            .coerceAtLeast(0)

    return when {

        minutes < 1 ->
            "Just now"

        minutes < 60 ->
            "$minutes min ago"

        minutes < 1440 -> {

            val hours = minutes / 60

            "$hours hour${if (hours == 1L) "" else "s"} ago"
        }

        else -> {

            val days = minutes / 1440

            "$days day${if (days == 1L) "" else "s"} ago"
        }
    }
}

@Composable
private fun rememberProviderNotifications(
    orders: List<Order>,
    foods: List<FoodListing>
): List<NotificationItem> {

    val primary =
        MaterialTheme.colorScheme.primary

    val primaryContainer =
        MaterialTheme.colorScheme.primaryContainer

    val secondary =
        MaterialTheme.colorScheme.secondary

    val secondaryContainer =
        MaterialTheme.colorScheme.secondaryContainer

    val now = Instant.now()
    val localNow = LocalTime.now()

    return remember(
        orders,
        localNow.hour,
        localNow.minute
    ) {

        buildList {
            orders.forEach { order ->
                //Only show recent orders.
                val createdAt =
                    runCatching {
                        Instant.parse(
                            order.createdAt
                        )
                    }.getOrNull()

                if (createdAt != null) {

                    val minutesAgo =
                        Duration
                            .between(
                                createdAt,
                                Instant.now()
                            )
                            .toMinutes()

                    if (
                        minutesAgo in 0..1440 &&
                        !order.status.equals(
                            "COMPLETED",
                            ignoreCase = true
                        )
                    ) {

                        add(
                            NotificationItem(
                                id = "new-order-${order.id}",
                                title = "New reservation received. Order #${order.orderCode} is waiting for pickup.",
                                timeAgo = relativeNotificationTime(
                                    createdAt, now
                                ),
                                iconResId = R.drawable.alarm_24dp_2854c5_fill0_wght400_grad0_opsz24,

                                containerColor = SecondaryBlue,
                                iconTint = PrimaryBlue
                            )
                        )
                    }
                }

                // 2. Pickup approaching
                val pickupStart =
                    runCatching {
                        parse(
                            order.pickupTime
                                .substringBefore("-")
                                .trim(),
                            DateTimeFormatter
                                .ofPattern(
                                    "hh:mm a",
                                    Locale.ENGLISH
                                )
                        )
                    }.getOrNull()

                if (pickupStart != null) {

                    val minutesUntil =
                        Duration
                            .between(
                                localNow,
                                pickupStart
                            )
                            .toMinutes()

                    if (
                        minutesUntil in 0..60 &&
                        !order.status.equals(
                            "COMPLETED",
                            ignoreCase = true
                        )
                    ) {

                        add(
                            NotificationItem(
                                id =
                                    "pickup-${order.id}",

                                title =
                                    "Pickup time for order #${order.id.takeLast(6)} is approaching.",

                                timeAgo =
                                    if (minutesUntil == 0L) {
                                        "Pickup time is now"
                                    } else {
                                        "Pickup starts in $minutesUntil min"
                                    },

                                iconResId =
                                    R.drawable
                                        .warning_24dp_f19e39_fill0_wght400_grad0_opsz24,

                                containerColor =
                                    primaryContainer,

                                iconTint =
                                    primary
                            )
                        )
                    }
                }
            }

            foods.forEach { food ->
                when{
                    food.quantity <= 0 ->{
                        add(
                            NotificationItem(
                                id = "sold-out-${food.id}",
                                title = "${food.name} is sold out",
                                timeAgo = "Food listing update",
                                iconResId = R.drawable.check_circle_24dp_cccccc_fill0_wght400_grad0_opsz24,
                                containerColor = SafeColor,
                                iconTint = PrimaryGreen
                            )
                        )
                    }
                    food.quantity <= 5 -> {
                        add(
                            NotificationItem(
                                id = "almost-sold-out${food.id}",
                                title = "${food.name} is almost sold out. Only ${food.quantity} left.",
                                timeAgo = "Food listing update",
                                iconResId = R.drawable.warning_24dp_f19e39_fill0_wght400_grad0_opsz24,
                                containerColor = SoonColor,
                                iconTint = textSoonColor
                            )
                        )
                    }
                }
            }
        }
    }
}