package com.example.assignment.data.repository

import com.example.assignment.model.Order
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlinx.serialization.Serializable

@Serializable
data class CreateOrderParams(
    val p_consumer_id: String,
    val p_food_id: String,
    val p_quantity: Int,
    val p_payment_success: Boolean
)
@Serializable
data class MarkDoneParams(
    val p_order_id: String,
    val p_provider_id: String,
    val p_pickup_code: String
)
@Serializable
data class GetProviderOrdersParams(
    val p_provider_id: String
)

class OrderRepository(
    private val supabase: SupabaseClient
) {

    suspend fun createOrder(
        consumerId: String,
        foodId: String,
        quantity: Int,
        paymentSuccess: Boolean
    ): Order {

        val params =
            CreateOrderParams(
                p_consumer_id = consumerId,
                p_food_id = foodId,
                p_quantity = quantity,
                p_payment_success = paymentSuccess
            )

        return supabase
            .postgrest
            .rpc(
                "create_food_order",
                params
            )
            .decodeSingle<Order>()
    }

    suspend fun getConsumerOrders(
        consumerId: String
    ): List<Order> {

        return supabase.postgrest
            .rpc(
                "get_consumer_orders",
                mapOf("p_consumer_id" to consumerId)
            )
            .decodeList()
    }
    suspend fun getAllConsumerOrders(
        consumerId: String
    ): List<Order> {

        return supabase.postgrest["orders"]
            .select {
                filter {
                    eq(
                        "consumer_id",
                        consumerId
                    )
                }
            }
            .decodeList<Order>()
    }

    suspend fun getProviderOrders(
        providerId: String
    ): List<Order> {
        return supabase.postgrest
            .rpc(
                "get_provider_orders",
                GetProviderOrdersParams(providerId)
            )
            .decodeList<Order>()
    }

    suspend fun markOrderDone(
        orderId: String,
        providerId: String,
        pickupCode: String
    ): Order? {
       supabase
            .postgrest
            .rpc(
                "mark_order_done",
                MarkDoneParams(
                    p_order_id = orderId,
                    p_provider_id = providerId,
                    p_pickup_code = pickupCode
                )
            )
        // Fetch the updated order after RPC
        return getOrderById(orderId)
    }

    suspend fun getOrderById(orderId: String): Order? {
        return supabase.postgrest["orders"]
            .select { filter { eq("id", orderId) } }
            .decodeSingleOrNull()
    }

    suspend fun cancelOrder(
        orderId: String,
        consumerId: String? = null,
        providerId: String? = null
    ) {
        supabase
            .postgrest["orders"]
            .update(
                {
                    set("status", "CANCELLED")
                }
            ) {
                filter {
                    eq("id", orderId)

                    if (!consumerId.isNullOrBlank()) {
                        eq("consumer_id", consumerId)
                    }

                    if (!providerId.isNullOrBlank()) {
                        eq("provider_id", providerId)
                    }
                }
            }
    }

    suspend fun expireOrder(
        orderId: String,
        providerId: String
    ) {
        supabase
            .postgrest["orders"]
            .update(
                {
                    set("status", "CANCELLED")
                    set("refund_status", "REFUND_PENDING")
                }
            ) {
                filter {
                    eq("id", orderId)
                    eq("provider_id", providerId)
                    eq("status", "PENDING")
                }
            }
    }

    suspend fun markRefunded(
        orderId: String,
        consumerId: String
    ) {
        supabase
            .postgrest["orders"]
            .update(
                {
                    set("refund_status", "REFUNDED")
                }
            ) {
                filter {
                    eq("id", orderId)
                    eq("consumer_id", consumerId)
                    eq("refund_status", "REFUND_PENDING")
                }
            }
    }

    suspend fun getOrdersByFoodId(
        foodId: String
    ): List<Order> {

        return supabase
            .postgrest["orders"]
            .select {
                filter {
                    eq("food_id", foodId)
                }
            }
            .decodeList<Order>()
    }
}