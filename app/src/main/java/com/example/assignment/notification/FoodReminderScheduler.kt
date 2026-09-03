package com.example.assignment.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

object FoodReminderScheduler {

    fun scheduleReminder(
        context: Context,
        itemId: String,
        saverId: String,
        foodName: String,
        expireDate: String,
        reminderDays: Int
    ) {

        val expiry = LocalDate.parse(expireDate)

        val reminderDate = expiry.minusDays(
            reminderDays.toLong()
        )

        val now = java.time.LocalDateTime.now()

        var reminderDateTime =
            reminderDate.atTime(LocalTime.of(9, 0))

        // If the reminder date is today but 9 AM has passed,
        // send the notification about 1 minute from now.
        if (
            reminderDate == LocalDate.now() &&
            reminderDateTime.isBefore(now)
        ) {
            reminderDateTime = now.plusMinutes(1)
        }

        val reminderTimeMillis = reminderDateTime
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        // If the reminder date is already in the past,
        // don't schedule anything.
        if (reminderTimeMillis <= System.currentTimeMillis()) {
            return
        }

        val intent = Intent(
            context,
            FoodReminderReceiver::class.java
        ).apply {

            putExtra("ITEM_ID", itemId)
            putExtra("SAVER_ID", saverId)
            putExtra("FOOD_NAME", foodName)
            putExtra("EXPIRE_DATE", expireDate)
            putExtra("REMINDER_DAYS", reminderDays)
            putExtra("ACTION", "REMINDER")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            itemId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager =
            context.getSystemService(
                Context.ALARM_SERVICE
            ) as AlarmManager

        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            reminderTimeMillis,
            pendingIntent
        )
    }

    fun cancelReminder(
        context: Context,
        itemId: String
    ) {
        val intent = Intent(
            context,
            FoodReminderReceiver::class.java
        )

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            itemId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    fun scheduleExpiry(
        context: Context,
        itemId: String,
        saverId: String,
        foodName: String,
        expireDate: String,
        reminderDays: Int
    ) {

        val expiry = LocalDate.parse(expireDate)

        val now = java.time.LocalDateTime.now()

        // Run shortly after midnight on expiry date
        var expiryDateTime = expiry.atTime(
            LocalTime.of(0, 1)
        )


        if (expiry == LocalDate.now() && expiryDateTime.isBefore(now)) {
            expiryDateTime = now.plusMinutes(1)
        }

        val expiryTimeMillis = expiryDateTime
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        // Don't schedule an expiry alarm in the past
        if (expiryTimeMillis <= System.currentTimeMillis()) {
            return
        }

        val intent = Intent(
            context,
            FoodReminderReceiver::class.java
        ).apply {

            putExtra("ITEM_ID", itemId)
            putExtra("SAVER_ID", saverId)
            putExtra("FOOD_NAME", foodName)
            putExtra("EXPIRE_DATE", expireDate)
            putExtra("REMINDER_DAYS", reminderDays)
            putExtra("ACTION", "EXPIRED")
        }

        /*
         * Different request code from the reminder alarm.
         *
         */
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            itemId.hashCode() + 1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager =
            context.getSystemService(
                Context.ALARM_SERVICE
            ) as AlarmManager

        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            expiryTimeMillis,
            pendingIntent
        )
    }
}