package com.example.assignment.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.assignment.R
import com.example.assignment.data.repository.InventoryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FoodReminderReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {

        val pendingResult = goAsync()

        val itemId =
            intent.getStringExtra("ITEM_ID") ?: return

        val foodName =
            intent.getStringExtra("FOOD_NAME") ?: "Food"

        val expireDate =
            intent.getStringExtra("EXPIRE_DATE") ?: ""

        val reminderDays =
            intent.getIntExtra("REMINDER_DAYS", 1)

        val action =
            intent.getStringExtra("ACTION") ?: "REMINDER"

        CoroutineScope(Dispatchers.IO).launch {

            try {

                val repository = InventoryRepository()

                when (action) {

                    "REMINDER" -> {

                        // Change status in Supabase
                        repository.updateStatus(
                            itemId = itemId,
                            saverId = intent.getStringExtra("SAVER_ID") ?: "",
                            status = "EXPIRING_SOON"
                        )

                        // Send notification
                        showReminderNotification(
                            context = context,
                            foodName = foodName,
                            expireDate = expireDate,
                            reminderDays = reminderDays
                        )
                    }

                    "EXPIRED" -> {

                        // Change status in Supabase
                        repository.updateStatus(
                            itemId = itemId,
                            saverId = intent.getStringExtra("SAVER_ID") ?: "",
                            status = "EXPIRED"
                        )

                        // Send expired notification (id +1 to avoid collision with reminder)
                        showExpiredNotification(
                            context = context,
                            foodName = foodName,
                            expireDate = expireDate
                        )
                    }
                }

            } catch (e: Exception) {

                e.printStackTrace()

            } finally {

                pendingResult.finish()
            }
        }
    }

    private fun showReminderNotification(
        context: Context,
        foodName: String,
        expireDate: String,
        reminderDays: Int
    ) {

        val channelId = "food_expiration_channel"

        val channel = NotificationChannel(
            channelId,
            "Food Expiration Reminders",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description =
                "Notifications for food approaching its expiry date"
        }

        val notificationManager =
            context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        notificationManager.createNotificationChannel(channel)

        val notification =
            NotificationCompat.Builder(
                context,
                channelId
            )
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Food Expiration Reminder")
                .setContentText("$foodName expires in " + "$reminderDays day(s) on $expireDate.")
                .setStyle(
                    NotificationCompat.BigTextStyle().bigText("$foodName is approaching its " + "expiry date. It will expire " + "on $expireDate.")
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()

        NotificationManagerCompat.from(context)
            .notify(foodName.hashCode(), notification)
    }

    private fun showExpiredNotification(
        context: Context,
        foodName: String,
        expireDate: String
    ) {

        val channelId = "food_expiration_channel"

        val channel = NotificationChannel(
            channelId,
            "Food Expiration Reminders",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications for food approaching its expiry date"
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(channel)

        val notification =
            NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Food Expired")
                .setContentText("$foodName expired on $expireDate. Please discard or handle it.")
                .setStyle(
                    NotificationCompat.BigTextStyle().bigText(
                        "$foodName has expired on $expireDate. Please discard it or handle it promptly."
                    )
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()

        NotificationManagerCompat.from(context)
            .notify(foodName.hashCode() + 1, notification)
    }
}