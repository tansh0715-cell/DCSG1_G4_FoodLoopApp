package com.example.assignment.screen

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment.R
import com.example.assignment.model.NotificationItem
import com.example.assignment.ui.theme.BackgroundColor
import com.example.assignment.ui.theme.PrimaryBlue
import com.example.assignment.ui.theme.PrimaryGreen
import com.example.assignment.ui.theme.PrimaryYellow
import com.example.assignment.ui.theme.SafeColor
import com.example.assignment.ui.theme.SecondaryBlue
import com.example.assignment.ui.theme.SecondaryYellow

@Composable
fun NotificationScreen(innerPadding: PaddingValues){
    val notifications = listOf(
        NotificationItem(
            "Your milk will expire soon. Use it soon to prevent food waste!",
            "2 hours ago",
            R.drawable.warning_24dp_f19e39_fill0_wght400_grad0_opsz24,
            SecondaryYellow,
            PrimaryYellow
        ),
        NotificationItem("Your Sushi Box pickup time is approaching. Don't forget!", "5 hours ago", R.drawable.alarm_24dp_2854c5_fill0_wght400_grad0_opsz24, SecondaryBlue, PrimaryBlue),
        NotificationItem("Congratulations! You saved 10 meals and earned the Food Saver badge!", "1 day ago", R.drawable.trophy_24dp_cccccc_fill0_wght400_grad0_opsz24, SafeColor, PrimaryGreen)
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(innerPadding)
            .padding(16.dp)
    ) {
        Text(
            text = "Notifications",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        //Notification list
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(notifications) { notification ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = BackgroundColor),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.2f))
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
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimary,
                                lineHeight = 20.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = notification.timeAgo,
                                style = MaterialTheme.typography.bodyMedium,
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
fun ProviderNotificationScreen(innerPadding: PaddingValues) {
    val providerNotifications = listOf(
        NotificationItem("New order! Order #RSV-001 pickup starts in 30 mins.", "10 mins ago", R.drawable.alarm_24dp_2854c5_fill0_wght400_grad0_opsz24,
            MaterialTheme.colorScheme.onTertiary, MaterialTheme.colorScheme.secondaryContainer),
        NotificationItem("Order #RSV-002 pickup window is ending soon.", "1 hour ago", R.drawable.warning_24dp_f19e39_fill0_wght400_grad0_opsz24,
            MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.tertiary),
        NotificationItem("Low stock alert! You have less than 3 items for 'Kaya Bun'.", "3 hours ago", R.drawable.add_circle_24dp_e3e3e3_fill0_wght400_grad0_opsz24,
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.primary)
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
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(providerNotifications) { notification ->
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