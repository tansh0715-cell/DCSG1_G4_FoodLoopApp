package com.example.assignment.notification

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object NotificationWorkerScheduler {

    private const val WORK_NAME =
        "foodloop_notification_worker"

    private const val NOW_WORK_NAME =
        "foodloop_notification_worker_now"

    private fun constraints():
            Constraints {

        return Constraints.Builder()
            .setRequiredNetworkType(
                NetworkType.CONNECTED
            )
            .build()
    }

    fun start(
        context: Context
    ) {

        val request =
            PeriodicWorkRequestBuilder<NotificationWorker>(
                15,
                TimeUnit.MINUTES
            )
                .setConstraints(
                    constraints()
                )
                .build()

        WorkManager
            .getInstance(context)
            .enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
    }

    fun runNow(
        context: Context
    ) {

        val request =
            OneTimeWorkRequestBuilder<NotificationWorker>()
                .setConstraints(
                    constraints()
                )
                .build()

        WorkManager
            .getInstance(context)
            .enqueueUniqueWork(
                NOW_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                request
            )
    }

    fun stop(
        context: Context
    ) {

        WorkManager
            .getInstance(context)
            .cancelUniqueWork(
                WORK_NAME
            )

        WorkManager
            .getInstance(context)
            .cancelUniqueWork(
                NOW_WORK_NAME
            )
    }
}