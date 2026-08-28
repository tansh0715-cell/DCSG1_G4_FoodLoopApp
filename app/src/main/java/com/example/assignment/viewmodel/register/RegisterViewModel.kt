package com.example.assignment.viewmodel.register

import android.net.Uri
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.assignment.data.repository.AuthRepository
import kotlinx.coroutines.launch

class RegisterViewModel(private val authRepository: AuthRepository) : ViewModel() {
    var name by mutableStateOf("")
    var email by mutableStateOf("")
    var phone by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")

    var restaurantName by mutableStateOf("")
    var address by mutableStateOf("")
    var licensePhotoUri by mutableStateOf<Uri?>(null)

    var message by mutableStateOf("")
    var isLoading by mutableStateOf(false)

    var providerLatitude by mutableStateOf<Double?>(null)
        private set
    var providerLongitude by mutableStateOf<Double?>(null)
        private set

    fun setProviderLocation(
        latitude: Double,
        longitude: Double
    ) {
        providerLatitude = latitude
        providerLongitude = longitude
    }

    fun register(accountType: String, onRegisterSuccess: () -> Unit) {
        if (password != confirmPassword) {
            message = "Passwords do not match"
            return
        }

        when (accountType) {
            "FOOD_SAVER" -> {
                if (name.isBlank() || email.isBlank() ||
                    phone.isBlank() || password.isBlank() ||
                    confirmPassword.isBlank()
                ) {
                    message = "Please fill in all fields"
                    return
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
                    message = "Please enter the valid email address"
                    return
                }
            }

            "FOOD_PROVIDER" -> {
                if (restaurantName.isBlank() || email.isBlank() ||
                    phone.isBlank() || address.isBlank() ||
                    password.isBlank() || confirmPassword.isBlank()
                ) {
                    message = "Please fill in all fields"
                    return
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
                    message = "Please enter the valid email address"
                    return
                }

                if (licensePhotoUri == null) {
                    message = "Please upload the license photo"
                    return
                }

                if (providerLatitude == null || providerLongitude == null) {
                    message = "Please allow location access before registering"
                    return
                }
            }
        }

        viewModelScope.launch {
            isLoading = true
            try {
                when (accountType) {
                    "FOOD_SAVER" -> {
                        authRepository.registerFoodSaver(
                            name = name.trim(),
                            email = email.trim().lowercase(),
                            phone = phone.trim(),
                            password = password
                        )
                    }

                    "FOOD_PROVIDER" -> {
                        authRepository.registerFoodProvider(
                            restaurantName = restaurantName.trim(),
                            email = email.trim().lowercase(),
                            phone = phone.trim(),
                            address = address.trim(),
                            licensePhoneUrl = licensePhotoUri.toString(),
                            password = password,
                            latitude = providerLatitude!!,
                            longitude = providerLongitude!!
                        )
                    }
                }
                message = "Registration successful"
                onRegisterSuccess()
            } catch (e: Exception) {
                message = e.message ?: "Registration failed"
            } finally {
                isLoading = false
            }
        }
    }

    companion object {
        fun Factory(authRepository: AuthRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                RegisterViewModel(authRepository)
            }
        }
    }
}