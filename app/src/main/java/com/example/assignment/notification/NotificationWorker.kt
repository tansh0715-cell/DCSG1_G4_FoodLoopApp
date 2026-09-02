package com.example.assignment.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.assignment.data.UserPreferencesManager
import com.example.assignment.data.repository.FoodRepository
import com.example.assignment.data.repository.OrderRepository
import com.example.assignment.data.supabase.supabase
import com.example.assignment.model.isPickupTimeEnded
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.first
import java.time.Duration
import java.time.Instant
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class NotificationWorker(
    appContext: Context, workerParams: WorkerParameters
) : CoroutineWorker(
    appContext, workerParams
) {

    private val context = appContext
    override suspend fun doWork(): Result {
        return try {
            NotificationHelper.createChannel(context)

            val preferences = UserPreferencesManager(context)
            val role = preferences.getUserRoleFlow().first()
            val userId = supabase.auth.currentUserOrNull()?.id

            if (role == null || userId == null) {
                return Result.success()
            }

            val orderRepository = OrderRepository(supabase)
            val foodRepository = FoodRepository(supabase)
            val eventStore = NotificationEventStore(context)

            when (role) {
                "FOOD_SAVER" -> {

                    checkConsumerNotifications(
                        userId = userId, orderRepository = orderRepository, eventStore = eventStore
                    )
                }

                "FOOD_PROVIDER" -> {
                    checkProviderNotifications(
                        userId = userId,
                        orderRepository = orderRepository,
                        foodRepository = foodRepository,
                        eventStore = eventStore
                    )
                }
            }

            Result.success()

        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    private suspend fun checkConsumerNotifications(
        userId: String, orderRepository: OrderRepository, eventStore: NotificationEventStore
    ) {

        val orders = orderRepository.getAllConsumerOrders(
            userId
        )

        for (order in orders) {
            // 1. Provider marked order as completed
            if (order.status.equals(
                    "COMPLETED", ignoreCase = true
                )
            ) {

                val eventId = "completed-${order.orderCode}"

                if (!eventStore.hasBeenShown(
                        eventId
                    )
                ) {

                    NotificationHelper.showNotification(
                        context = context,
                        notificationId = eventId.hashCode(),
                        title = "Food Collected",
                        message = "Your food for order #${order.orderCode} has been successfully collected.",
                        eventId = eventId,
                        ownerId = userId,
                        role = "FOOD_SAVER"
                    )

                    eventStore.markAsShown(
                        eventId
                    )
                }
            }

            // 2. Pickup time has passed
            if (!order.status.equals(
                    "PENDING", ignoreCase = true
                ) &&
                order.isPickupTimeEnded()
            ) {

                val eventId = "pickup-passed-${order.orderCode}"

                if (!eventStore.hasBeenShown(eventId)) {

                    NotificationHelper.showNotification(
                        context = context,
                        notificationId =
                            eventId.hashCode(),

                        title =
                            "Pickup Time Passed",

                        message =
                            "Order #${order.orderCode} was not collected. Refund: RM ${
                                String.format(
                                    "%.2f",
                                    order.totalPrice
                                )
                            }",

                        eventId =
                            eventId,

                        ownerId =
                            order.consumerId,

                        role =
                            "FOOD_SAVER"
                    )

                    eventStore.markAsShown(
                        eventId
                    )
                }
            }
        }
    }

    private suspend fun checkProviderNotifications(
        userId: String,
        orderRepository: OrderRepository,
        foodRepository: FoodRepository,
        eventStore: NotificationEventStore
    ) {

        // 1. New consumer orders
        val orders = orderRepository.getProviderOrders(
            userId
        )
        // 2. Pickup time has passed
        for (order in orders) {

            if (
                order.status.equals(
                    "PENDING",
                    ignoreCase = true
                ) &&
                order.isPickupTimeEnded()
            ) {

                val eventId =
                    "provider-order-canceled-${order.orderCode}"

                if (
                    !eventStore.hasBeenShown(
                        eventId
                    )
                ) {

                    val posted =
                        NotificationHelper.showNotification(

                            context = context,

                            notificationId =
                                eventId.hashCode(),

                            title =
                                "Order Canceled",

                            message =
                                "Order #${order.orderCode} was canceled because the pickup time expired.",

                            eventId =
                                eventId,

                            ownerId =
                                userId,

                            role =
                                "FOOD_PROVIDER"
                        )

                    if (posted) {

                        eventStore.markAsShown(
                            eventId
                        )
                    }
                }
            }
        }

        for (order in orders) {

            val eventId = "new-order-${order.orderCode}"

            //Only consider recent orders, prevent all old orders becoming notifications, after installing the new system
            val createdAt = runCatching {
                Instant.parse(
                    order.createdAt
                )
            }.getOrNull()

            if (createdAt != null) {

                val minutesAgo = Duration.between(
                    createdAt, Instant.now()
                ).toMinutes()

                if (minutesAgo <= 15 && !eventStore.hasBeenShown(
                        eventId
                    )
                ) {

                    NotificationHelper.showNotification(
                        context = context,
                        notificationId = eventId.hashCode(),
                        title = "New Consumer Order",
                        message = "Order #${order.orderCode} has been placed.",
                        eventId = eventId,
                        ownerId = userId,
                        role = "FOOD_PROVIDER"
                    )

                    eventStore.markAsShown(
                        eventId
                    )
                }
            }
        }

        // 2. Low stock / sold out
        val foods = foodRepository.getFoodListingByProvider(
            userId
        )

        for (food in foods) {

            if (food.quantity <= 0) {

                val eventId = "sold-out-${food.id}"

                if (!eventStore.hasBeenShown(
                        eventId
                    )
                ) {

                    NotificationHelper.showNotification(
                        context = context,
                        notificationId = eventId.hashCode(),
                        title = "Food Sold Out",
                        message = "${food.name} is sold out.",
                        eventId = eventId,
                        ownerId = userId,
                        role = "FOOD_PROVIDER"
                    )

                    eventStore.markAsShown(
                        eventId
                    )
                }

            } else if (food.quantity <= 5) {

                val eventId = "low-stock-${food.id}"

                if (!eventStore.hasBeenShown(
                        eventId
                    )
                ) {

                    val posted = NotificationHelper.showNotification(
                        context = context,
                        notificationId = eventId.hashCode(),
                        title = "Low Stock Alert",
                        message = "${food.name} has only ${food.quantity} item(s) left.",
                        eventId = eventId,
                        ownerId = userId,
                        role = "FOOD_PROVIDER"
                    )

                    if (posted) {
                        eventStore.markAsShown(
                            eventId
                        )
                    }
                }
            }
        }
    }
}