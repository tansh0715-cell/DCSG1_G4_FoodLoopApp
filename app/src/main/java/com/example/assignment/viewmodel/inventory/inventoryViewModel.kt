package com.example.assignment.viewmodel.inventory

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.data.repository.InventoryRepository
import com.example.assignment.model.inventoryModule.Food
import com.example.assignment.model.inventoryModule.FoodInput
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import android.content.Context
import com.example.assignment.notification.FoodReminderScheduler
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.datetime.minus
import kotlin.time.Clock

class InventoryViewModel(val saverId: String) : ViewModel() {
    private val repository = InventoryRepository()
    var foods by mutableStateOf<List<Food>>(emptyList()) //Data

     var selectedFilter by mutableStateOf(0) //Filter
     var isLoading by mutableStateOf(true) //Loading
     var message by mutableStateOf("")

    val filteredFoods: List<Food>
        get() = when (selectedFilter) {
            0 -> foods
            1 -> foods.filter { it.status == "SAFE" }
            2 -> foods.filter { it.status == "EXPIRING_SOON" }
            3 -> foods.filter { it.status == "EXPIRED" }
            else -> foods
        }


    fun loadInventory(context: Context) {

        viewModelScope.launch {

            isLoading = true
            message = ""

            try {

                val loadedFoods =
                    repository.getInventory(saverId)

                loadedFoods.forEach { food ->

                    // Make sure Supabase has the correct status
                    updateFoodStatusIfNeeded(food)

                    // Schedule reminder
                    FoodReminderScheduler.scheduleReminder(
                        context = context,
                        itemId = food.item_id,
                        saverId = saverId,
                        foodName = food.name,
                        expireDate = food.expireDate.toString(),
                        reminderDays = food.reminder_days
                    )

                    // Schedule expiry
                    FoodReminderScheduler.scheduleExpiry(
                        context = context,
                        itemId = food.item_id,
                        saverId = saverId,
                        foodName = food.name,
                        expireDate = food.expireDate.toString(),
                        reminderDays = food.reminder_days
                    )
                }

                // Reload the records so local UI has
                // the updated statuses
                foods = repository.getInventory(saverId)

            } catch (e: Exception) {

                println(e.message)

                message =
                    e.message ?: "An unknown error occurred"

            } finally {

                isLoading = false
            }
        }
    }

    @OptIn(FormatStringsInDatetimeFormats::class)
    fun addItem(
        context: Context,
        foodName: String,
        reminder_days: Int,
        expireDate: String,
        imageBytes: ByteArray? = null,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {

            isLoading = true
            message = ""

            try {

                val imageUrl = imageBytes?.let {
                    repository.uploadFoodImage(
                        saverId,
                        foodName,
                        it
                    )
                }

                val food = repository.addItem(
                    FoodInput(
                        saverId,
                        foodName,
                        reminder_days,
                        "SAFE",
                        LocalDate.parse(expireDate),
                        imageUrl
                    )
                )

                foods = foods + food

                FoodReminderScheduler.scheduleReminder(
                    context = context,
                    itemId = food.item_id,
                    saverId = saverId,
                    foodName = food.name,
                    expireDate = food.expireDate.toString(),
                    reminderDays = food.reminder_days
                )

                FoodReminderScheduler.scheduleExpiry(
                    context = context,
                    itemId = food.item_id,
                    saverId = saverId,
                    foodName = food.name,
                    expireDate = food.expireDate.toString(),
                    reminderDays = food.reminder_days
                )

                message = "Item added successfully"

                // Only navigate AFTER everything succeeds
                onSuccess()

            } catch (e: Exception) {

                e.printStackTrace()
                message = "Failed to add item: ${e.message}"

            } finally {
                isLoading = false
            }
        }
    }

    fun deleteItem(
        context: Context,
        itemId: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {

            isLoading = true
            message = ""

            try {

                // Cancel the scheduled notification first
                FoodReminderScheduler.cancelReminder(
                    context = context,
                    itemId = itemId
                )

                // Delete from Supabase
                repository.deleteItem(
                    itemId = itemId,
                    saverId = saverId
                )

                // Remove from local list
                foods = foods.filter {
                    it.item_id != itemId
                }

                message = "Item deleted successfully"
                onSuccess()

            } catch (e: Exception) {

                println(e.message)
                message = "Failed to delete item"

            } finally {

                isLoading = false
            }
        }
    }
    fun selectFilter(filter: Int) {
        selectedFilter = filter
    }

    fun updateReminder(
        context: Context,
        food: Food,
        newReminderDays: Int,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {

            isLoading = true
            message = ""

            try {

                repository.updateReminderDays(
                    itemId = food.item_id,
                    saverId = saverId,
                    reminderDays = newReminderDays
                )

                // Cancel old reminder
                FoodReminderScheduler.cancelReminder(
                    context = context,
                    itemId = food.item_id
                )

                // Schedule new reminder
                FoodReminderScheduler.scheduleReminder(
                    context = context,
                    itemId = food.item_id,
                    saverId = saverId,
                    foodName = food.name,
                    expireDate = food.expireDate.toString(),
                    reminderDays = newReminderDays
                )

                foods = foods.map {

                    if (it.item_id == food.item_id) {

                        it.copy(
                            reminder_days = newReminderDays
                        )

                    } else {

                        it
                    }
                }

                message =
                    "Reminder updated successfully"
                onSuccess()

            } catch (e: Exception) {

                println(e.message)

                message =
                    "Failed to update reminder"

            } finally {

                isLoading = false
            }
        }
    }
    private suspend fun updateFoodStatusIfNeeded(
        food: Food
    ) {
        val today = Clock.System
            .todayIn(TimeZone.currentSystemDefault())

        val expiryDate = food.expireDate

        val reminderDate = expiryDate.minus(
            DatePeriod(days = food.reminder_days)
        )

        val newStatus = when {

            today >= expiryDate -> {
                "EXPIRED"
            }

            today >= reminderDate -> {
                "EXPIRING_SOON"
            }

            else -> {
                "SAFE"
            }
        }

        if (food.status != newStatus) {

            repository.updateStatus(
                itemId = food.item_id,
                saverId = saverId,
                status = newStatus
            )
        }
    }

}