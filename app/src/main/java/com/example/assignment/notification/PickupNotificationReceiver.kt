package com.example.assignment.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.assignment.data.repository.OrderRepository
import com.example.assignment.data.supabase.supabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PickupNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {

        val orderId =
            intent.getStringExtra("ORDER_ID")
                ?: return

        val orderCode =
            intent.getStringExtra("ORDER_CODE")
                ?: orderId

        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {

            try {

                val repository =
                    OrderRepository(supabase)

                val order =
                    repository.getOrderById(
                        orderId
                    )

                if (order == null) {
                    return@launch
                }

                //Provider already collected the food ->do not show refund notification.
                if (
                    order.status.equals(
                        "COMPLETED",
                        ignoreCase = true
                    )
                ) {
                    return@launch
                }

                /*
                 * Only notify if the order
                 * is still pending.
                 */
                if (
                    !order.status.equals(
                        "PENDING",
                        ignoreCase = true
                    )
                ) {
                    return@launch
                }

                val eventId =
                    "refund-${order.orderCode}"

                val eventStore =
                    NotificationEventStore(
                        context
                    )

                //Show only once.
                if (
                    eventStore.hasBeenShown(
                        eventId
                    )
                ) {
                    return@launch
                }

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
                    eventId = eventId,
                    ownerId = order.consumerId,
                    role = "FOOD_SAVER"
                )

                eventStore.markAsShown(
                    eventId
                )

            } catch (e: Exception) {

                e.printStackTrace()

            } finally {

                pendingResult.finish()
            }
        }
    }
}