package com.example.assignment.state

data class AddFoodUiState(
    val foodName: String = "",
    val selectedCategory: String = "Select Category",
    val quantity: String = "",
    val description: String = "",
    val originalPrice: String = "",
    val selectedDiscount: String = "",

    val pickupTime: String  = "",

    val nameError: String? = null,
    val categoryError: String? = null,
    val qtyError: String? = null,
    val priceError: String? = null,
    val pickupTimeError: String? = null,

    val showConfirmDialog: Boolean = false
)

//only allow the specific events that are defined
sealed class AddFoodEvent{
    data class ShowToast(
        val message: String
    ): AddFoodEvent()
    object NavigateBack: AddFoodEvent() //return after successful publishing
}
