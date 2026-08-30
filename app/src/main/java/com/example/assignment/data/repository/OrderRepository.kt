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
    val p_provider_id: String
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
        quantity: Int
    ): Order {

        val params = CreateOrderParams(
            p_consumer_id = consumerId,
            p_food_id = foodId,
            p_quantity = quantity,
            p_payment_success = true
        )

        return supabase.postgrest
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
        providerId: String
    ): Order {

        return supabase.postgrest
            .rpc(
                "mark_order_done",
                MarkDoneParams(p_order_id = orderId, p_provider_id = providerId)
            )
            .decodeSingle()
    }

    suspend fun getOrderById(orderId: String): Order? {
        return supabase.postgrest["orders"]
            .select { filter { eq("id", orderId) } }
            .decodeSingleOrNull()
    }
}