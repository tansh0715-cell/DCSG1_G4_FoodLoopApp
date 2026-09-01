package com.example.assignment.viewmodel.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.data.repository.ProfileRepository
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: ProfileRepository,
    private val userId: String
) : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var isSaving by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var successMessage by mutableStateOf<String?>(null)
        private set

    var role by mutableStateOf<String?>(null)
        private set

    // Common
    var email by mutableStateOf("")
        private set

    // Saver fields (editable)
    var name by mutableStateOf("")
    var nameError by mutableStateOf<String?>(null)

    // Provider fields (editable)
    var restaurantName by mutableStateOf("")
    var restaurantNameError by mutableStateOf<String?>(null)

    var phone by mutableStateOf("")
    var phoneError by mutableStateOf<String?>(null)

    var address by mutableStateOf("")
    var addressError by mutableStateOf<String?>(null)

    // Provider read-only
    var licensePhotoUri by mutableStateOf("")
        private set

    var isProfileLoaded by mutableStateOf(false)
        private set

    init {
        loadProfile()
    }

    fun loadProfile() {
        if (userId.isBlank()) {
            errorMessage = "User not logged in"
            return
        }
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val saver = repository.getFoodSaver(userId)
                if (saver != null) {
                    role = "FOOD_SAVER"
                    name = saver.name
                    email = saver.email
                    phone = saver.phone
                    isProfileLoaded = true
                } else {
                    val provider = repository.getFoodProvider(userId)
                    if (provider != null) {
                        role = "FOOD_PROVIDER"
                        restaurantName = provider.restaurantName
                        email = provider.email
                        phone = provider.phone
                        address = provider.address
                        licensePhotoUri = provider.licensePhotoUri
                        isProfileLoaded = true
                    } else {
                        errorMessage = "Profile not found"
                    }
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to load profile"
            } finally {
                isLoading = false
            }
        }
    }

    fun clearMessages() {
        errorMessage = null
        successMessage = null
    }

    fun clearFieldErrors() {
        nameError = null
        restaurantNameError = null
        phoneError = null
        addressError = null
    }

    private fun validate(): Boolean {
        clearFieldErrors()
        var isValid = true

        if (role == "FOOD_SAVER") {
            if (name.isBlank()) {
                nameError = "Name is required"
                isValid = false
            } else if (name.trim().length < 2) {
                nameError = "Name must be at least 2 characters"
                isValid = false
            }
        } else if (role == "FOOD_PROVIDER") {
            if (restaurantName.isBlank()) {
                restaurantNameError = "Restaurant name is required"
                isValid = false
            } else if (restaurantName.trim().length < 2) {
                restaurantNameError = "Restaurant name must be at least 2 characters"
                isValid = false
            }
            if (address.isBlank()) {
                addressError = "Address is required"
                isValid = false
            } else if (address.trim().length < 5) {
                addressError = "Address is too short"
                isValid = false
            }
        }

        if (phone.isBlank()) {
            phoneError = "Phone is required"
            isValid = false
        } else if (!phone.matches(Regex("^[0-9+\\- ]{8,20}$"))) {
            phoneError = "Enter valid phone (8-20 digits)"
            isValid = false
        }

        return isValid
    }

    fun saveProfile(onSuccess: () -> Unit = {}) {
        if (!validate()) return

        viewModelScope.launch {
            isSaving = true
            errorMessage = null
            successMessage = null
            try {
                when (role) {
                    "FOOD_SAVER" -> {
                        repository.updateFoodSaver(
                            userId = userId,
                            name = name,
                            phone = phone
                        )
                    }
                    "FOOD_PROVIDER" -> {
                        repository.updateFoodProvider(
                            userId = userId,
                            restaurantName = restaurantName,
                            phone = phone,
                            address = address
                        )
                    }
                    else -> throw Exception("Unknown user role")
                }
                successMessage = "Profile updated successfully"
                onSuccess()
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to update profile"
            } finally {
                isSaving = false
            }
        }
    }
}
