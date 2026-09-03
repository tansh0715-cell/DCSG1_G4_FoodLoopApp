package com.example.assignment.data.repository

import com.example.assignment.data.supabase.supabase
import com.example.assignment.model.inventoryModule.Food
import com.example.assignment.model.inventoryModule.FoodInput
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.Serializable

class InventoryRepository{
    suspend fun getInventory(saverId: String): List<Food> {
        return supabase.from("inventory")
            .select{
                filter {
                    eq("saver_id", saverId) }
            }.decodeList<Food>()

    }

    suspend fun addItem(item: FoodInput): Food {
        return supabase.from("inventory")
            .insert(item){
                select()
            }
            .decodeSingle<Food>()
    }

    suspend fun deleteItem(itemId: String, saverId: String){
        supabase.from("inventory")
            .delete {
                filter {
                    eq("item_id", itemId)
                    eq("saver_id", saverId)
                }
            }
    }

    suspend fun uploadFoodImage(
        saverId: String,
        foodName: String,
        imageBytes: ByteArray
    ): String {
        val fileName = "${foodName.replace(" ", "_")}_${System.currentTimeMillis()}.jpg"
        val path = "$saverId/$fileName"
        val bucket = supabase.storage["inventory-images"]

        bucket.upload(path, imageBytes) {
            upsert = false
        }
        return bucket.publicUrl(path)
    }

    suspend fun updateReminderDays(
        itemId: String,
        saverId: String,
        reminderDays: Int
    ) {
        supabase.from("inventory")
            .update(
                {
                    set("reminder_days", reminderDays)
                }
            ) {
                filter {
                    eq("item_id", itemId)
                    eq("saver_id", saverId)
                }
            }
    }

    suspend fun updateStatus(
        itemId: String,
        saverId: String,
        status: String
    ) {
        supabase
            .from("inventory")
            .update(
                {
                    set("status", status)
                }
            ) {
                filter {
                    eq("item_id", itemId)
                    eq("saver_id", saverId)
                }
            }
    }
}
