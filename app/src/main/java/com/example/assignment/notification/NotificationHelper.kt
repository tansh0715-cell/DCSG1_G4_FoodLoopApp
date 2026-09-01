package com.example.assignment.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.assignment.R

object NotificationHelper {

    private const val CHANNEL_ID =
        "foodloop_notification_channel"

    fun createChannel(
        context: Context
    ) {

        val channel =
            NotificationChannel(
                CHANNEL_ID,
                "FoodLoop Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {

                description =
                    "FoodLoop order and food notifications"
            }

        val manager =
            context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        manager.createNotificationChannel(
            channel
        )
    }

    fun showNotification(
        context: Context,
        notificationId: Int,
        title: String,
        message: String,
        eventId: String,
        ownerId: String,
        role: String
    ): Boolean {

        // ALWAYS save notification history
        NotificationHistoryStore(
            context
        ).saveNotification(

            StoredNotification(

                id = eventId,

                title = title,

                message = message,

                timestamp =
                    System.currentTimeMillis(),

                ownerId = ownerId,

                role = role
            )
        )

        // Android 13+
        if (
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return false
        }

        return try {

            createChannel(context)

            val notification =
                NotificationCompat.Builder(
                    context,
                    CHANNEL_ID
                )
                    .setSmallIcon(
                        R.drawable.ic_notification
                    )
                    .setContentTitle(
                        title
                    )
                    .setContentText(
                        message
                    )
                    .setStyle(
                        NotificationCompat
                            .BigTextStyle()
                            .bigText(message)
                    )
                    .setPriority(
                        NotificationCompat
                            .PRIORITY_HIGH
                    )
                    .setAutoCancel(true)
                    .build()

            NotificationManagerCompat
                .from(context)
                .notify(
                    notificationId,
                    notification
                )

            true

        } catch (
            e: SecurityException
        ) {

            false
        }
    }
}