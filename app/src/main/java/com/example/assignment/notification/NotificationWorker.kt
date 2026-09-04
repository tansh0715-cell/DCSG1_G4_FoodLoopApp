package com.example.assignment.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.assignment.data.UserPreferencesManager
import com.example.assignment.data.repository.FoodRepository
import com.example.assignment.data.repository.OrderRepository
import com.example.assignment.data.supabase.supabase
import com.example.assignment.model.Order
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.first
import java.time.Duration
import java.time.Instant
import java.time.Instant.parse
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
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
                        userId = userId,
                        orderRepository = orderRepository,
                        eventStore = eventStore
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
            Result.failure()
        }
    }

    private suspend fun checkConsumerNotifications(
        userId: String,
        orderRepository: OrderRepository,
        eventStore: NotificationEventStore
    ) {

        val orders =
            orderRepository.getAllConsumerOrders(
                userId
            )

        for (order in orders) {

            // =========================================
            // 1. Handle expired pickup
            // =========================================

            if (
                order.status.equals("PENDING", ignoreCase = true) &&
                isOrderPickupTimeEnded(order)
            ) {
                runCatching {
                    orderRepository.expireOrder(
                        orderId = order.id,
                        providerId = order.providerId
                    )
                }.onFailure {
                    it.printStackTrace()
                }
            }

            // =========================================
            // 2. Get latest order status
            // =========================================

            val updatedOrder =
                orderRepository.getOrderById(
                    order.id
                ) ?: continue

            // =========================================
            // 3. Refund notification
            // =========================================

            if (
                !updatedOrder.status.equals(
                    "CANCELLED",
                    ignoreCase = true
                ) ||
                !updatedOrder.refundStatus.equals(
                    "REFUND_PENDING",
                    ignoreCase = true
                )
            ) {
                continue
            }

            val eventId =
                "refund-${updatedOrder.orderCode}"

            if (
                eventStore.hasBeenShown(eventId)
            ) {
                continue
            }

            val posted =
                NotificationHelper.showNotification(
                    context = context,
                    notificationId =
                        eventId.hashCode(),
                    title = "Refund Processed",
                    message =
                        "Order #${updatedOrder.orderCode} was canceled because the pickup time expired. Refund: RM ${
                            String.format(
                                "%.2f",
                                updatedOrder.totalPrice
                            )
                        }",
                    eventId = eventId,
                    ownerId = userId,
                    role = "FOOD_SAVER"
                )

            if (posted) {

                runCatching {

                    orderRepository.markRefunded(
                        orderId = updatedOrder.id,
                        consumerId = userId
                    )

                    eventStore.markAsShown(
                        eventId
                    )

                }.onFailure {
                    it.printStackTrace()
                }
            }
        }
    }

    private fun isOrderPickupTimeEnded(
        order: Order
    ): Boolean {
        return try {

            val formatter = DateTimeFormatter.ofPattern(
                "h:mm a",
                Locale.ENGLISH
            )

            val parts = order.pickupTime.split(" - ")

            if (parts.size != 2) {
                return false
            }

            val start = LocalTime.parse(
                parts[0].trim(),
                formatter
            )

            val end = LocalTime.parse(
                parts[1].trim(),
                formatter
            )

            val orderDate =
                parse(order.createdAt)
                    .atZone(
                        ZoneId.of("Asia/Kuala_Lumpur")
                    )
                    .toLocalDate()

            val endDate =
                if (end.isBefore(start)) {
                    orderDate.plusDays(1)
                } else {
                    orderDate
                }

            val pickupEnd =
                LocalDateTime.of(
                    endDate,
                    end
                )

            LocalDateTime.now(
                ZoneId.of("Asia/Kuala_Lumpur")
            ).isAfter(pickupEnd)

        } catch (e: Exception) {
            false
        }
    }

    private suspend fun checkProviderNotifications(
        userId: String,
        orderRepository: OrderRepository,
        foodRepository: FoodRepository,
        eventStore: NotificationEventStore
    ) {

        // New consumer orders
        val orders = orderRepository.getProviderOrders(
            userId
        )
        // Pickup time has passed
        for (order in orders) {

            if (
                !order.status.equals(
                    "PENDING",
                    ignoreCase = true
                )
            ) {
                continue
            }

            if (isOrderPickupTimeEnded(order)) {

                val eventId =
                    "provider-order-canceled-${order.orderCode}"

                if (
                    !eventStore.hasBeenShown(eventId)
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

                // Change PENDING -> CANCELLED
                // and mark refund as pending
                runCatching {
                    orderRepository.expireOrder(
                        orderId = order.id,
                        providerId = userId
                    )
                }.onFailure {
                    it.printStackTrace()
                }
            }
        }

        // New order notifications
        for (order in orders) {

            // Only PENDING orders can generate a new-order notification
            if (
                !order.status.equals(
                    "PENDING",
                    ignoreCase = true
                )
            ) {
                continue
            }

            val eventId = "new-order-${order.orderCode}"

            val createdAt = runCatching {
                parse(order.createdAt)
            }.getOrNull()

            if (createdAt != null) {

                val minutesAgo = Duration.between(
                    createdAt,
                    Instant.now()
                ).toMinutes()

                if (
                    minutesAgo in 0..15 &&
                    !eventStore.hasBeenShown(eventId)
                ) {

                    val posted =
                        NotificationHelper.showNotification(
                            context = context,
                            notificationId = eventId.hashCode(),
                            title = "New Consumer Order",
                            message = "Order #${order.orderCode} has been placed.",
                            eventId = eventId,
                            ownerId = userId,
                            role = "FOOD_PROVIDER"
                        )

                    if (posted) {
                        eventStore.markAsShown(eventId)
                    }
                }
            }
        }

        // Low stock / sold out
        val foods = foodRepository.getFoodListingByProvider(
            userId
        )

        for (food in foods) {
            // Sold out
            if (food.quantity <= 0) {

                val eventId =
                    "sold-out-${food.id}"

                if (
                    !eventStore.hasBeenShown(eventId)
                ) {

                    val posted =
                        NotificationHelper.showNotification(
                            context = context,
                            notificationId =
                                eventId.hashCode(),
                            title =
                                "Food Sold Out",
                            message =
                                "${food.name} is sold out.",
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

            } else if (food.quantity <= 5) {

                val eventId =
                    "low-stock-${food.id}"

                if (
                    !eventStore.hasBeenShown(eventId)
                ) {

                    val posted =
                        NotificationHelper.showNotification(
                            context = context,
                            notificationId =
                                eventId.hashCode(),
                            title =
                                "Low Stock Alert",
                            message =
                                "${food.name} has only ${food.quantity} item(s) left.",
                            eventId =
                                eventId,
                            ownerId =
                                userId,
                            role =
                                "FOOD_PROVIDER"
                        )

                    if (posted) {
                        eventStore.markAsShown(eventId)
                    }
                }
            }
        }
    }
}