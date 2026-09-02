package com.example.assignment.viewmodel.register

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.graphics.scale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.assignment.data.repository.AuthRepository
import com.example.assignment.data.repository.LicensePhotoRepository
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class RegisterViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    var name by mutableStateOf("")
    var email by mutableStateOf("")
    var phone by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")

    var restaurantName by mutableStateOf("")
    var address by mutableStateOf("")
    var licensePhotoUri by mutableStateOf<Uri?>(null)

    var providerLatitude by mutableStateOf<Double?>(null)
        private set

    var providerLongitude by mutableStateOf<Double?>(null)
        private set

    var isLoading by mutableStateOf(false)

    var message by mutableStateOf<String?>(null)
        private set

    // Field Errors
    var nameError by mutableStateOf<String?>(null)
        private set

    var emailError by mutableStateOf<String?>(null)
        private set

    var phoneError by mutableStateOf<String?>(null)
        private set

    var passwordError by mutableStateOf<String?>(null)
        private set

    var confirmPasswordError by mutableStateOf<String?>(null)
        private set

    var restaurantNameError by mutableStateOf<String?>(null)
        private set

    var addressError by mutableStateOf<String?>(null)
        private set

    var licensePhotoError by mutableStateOf<String?>(null)
        private set

    var locationError by mutableStateOf<String?>(null)
        private set

    private val licensePhotoRepository =
        LicensePhotoRepository()

    fun clearMessage() {
        message = null
    }

    // Clear All Errors
    fun clearErrors() {
        nameError = null
        emailError = null
        phoneError = null
        passwordError = null
        confirmPasswordError = null
        restaurantNameError = null
        addressError = null
        licensePhotoError = null
        locationError = null
    }

    // Clear Individual Errors
    fun clearNameError() {
        nameError = null
    }

    fun clearEmailError() {
        emailError = null
    }

    fun clearPhoneError() {
        phoneError = null
    }

    fun clearPasswordError() {
        passwordError = null
    }

    fun clearConfirmPasswordError() {
        confirmPasswordError = null
    }

    fun clearRestaurantNameError() {
        restaurantNameError = null
    }

    fun clearAddressError() {
        addressError = null
    }

    fun clearLicensePhotoError() {
        licensePhotoError = null
    }

    // Location
    fun setProviderLocation(
        latitude: Double,
        longitude: Double
    ) {
        providerLatitude = latitude
        providerLongitude = longitude
        locationError = null
    }

    fun isPhoneValid(phone: String): Boolean {
        return Regex("^01[0-9]{8,9}$")
            .matches(phone)
    }

    // Common Field Validation
    private fun validateCommonFields(): Boolean {

        var valid = true

        // Email
        if (email.isBlank()) {

            emailError =
                "Please enter your email address"

            valid = false

        } else if (
            !Patterns.EMAIL_ADDRESS
                .matcher(email.trim())
                .matches()
        ) {

            emailError =
                "Please enter a valid email address"

            valid = false
        }

        // Phone
        if (phone.isBlank()) {

            phoneError =
                "Please enter your phone number"

            valid = false

        } else if (!isPhoneValid(phone.trim())) {

            phoneError =
                "Invalid phone number"

            valid = false
        }

        // Password
        if (password.isBlank()) {

            passwordError =
                "Please enter your password"

            valid = false
        }

        // Confirm Password
        if (confirmPassword.isBlank()) {

            confirmPasswordError =
                "Please confirm your password"

            valid = false

        } else if (password != confirmPassword) {

            confirmPasswordError =
                "Passwords do not match"

            valid = false
        }

        return valid
    }

    // Food Saver Validation
    private fun validateFoodSaver(): Boolean {

        var valid = true

        if (name.isBlank()) {

            nameError =
                "Please enter your name"

            valid = false
        }

        if (!validateCommonFields()) {
            valid = false
        }

        return valid
    }

    // Food Provider Validation
    private fun validateFoodProvider(): Boolean {

        var valid = true

        if (restaurantName.isBlank()) {

            restaurantNameError =
                "Please enter the restaurant name"

            valid = false
        }

        if (address.isBlank()) {

            addressError =
                "Please enter the restaurant address"

            valid = false
        }

        if (licensePhotoUri == null) {

            licensePhotoError =
                "Please upload the license photo"

            valid = false
        }

        if (
            providerLatitude == null ||
            providerLongitude == null
        ) {

            locationError =
                "Please allow location access before registering"

            valid = false
        }

        if (!validateCommonFields()) {
            valid = false
        }

        return valid
    }

    // Compress License Photo
    private fun compressImage(
        imageBytes: ByteArray
    ): ByteArray {

        val originalBitmap =
            BitmapFactory.decodeByteArray(
                imageBytes,
                0,
                imageBytes.size
            ) ?: throw Exception(
                "Unable to process selected license photo."
            )

        val maxSize = 2000

        val width = originalBitmap.width
        val height = originalBitmap.height

        val scale = minOf(
            maxSize.toFloat() / width,
            maxSize.toFloat() / height,
            1f
        )

        val newWidth =
            (width * scale).toInt()

        val newHeight =
            (height * scale).toInt()

        val resizedBitmap =
            originalBitmap.scale(
                newWidth,
                newHeight
            )

        val outputStream =
            ByteArrayOutputStream()

        resizedBitmap.compress(
            Bitmap.CompressFormat.JPEG,
            90,
            outputStream
        )

        originalBitmap.recycle()
        resizedBitmap.recycle()

        return outputStream.toByteArray()
    }

    // Register
    fun register(
        accountType: String,
        context: Context,
        onRegisterSuccess: () -> Unit
    ) {

        clearErrors()

        val isValid = when (accountType) {

            "FOOD_SAVER" -> {
                validateFoodSaver()
            }

            "FOOD_PROVIDER" -> {
                validateFoodProvider()
            }

            else -> {
                false
            }
        }

        // Stop registration if validation fails
        if (!isValid) {
            return
        }

        viewModelScope.launch {

            isLoading = true

            try {

                when (accountType) {

                    // =========================
                    // FOOD SAVER
                    // =========================
                    "FOOD_SAVER" -> {

                        authRepository.registerFoodSaver(
                            name = name.trim(),
                            email = email
                                .trim()
                                .lowercase(),
                            phone = phone.trim(),
                            password = password
                        )
                    }

                    // =========================
                    // FOOD PROVIDER
                    // =========================
                    "FOOD_PROVIDER" -> {

                        val photoUri =
                            licensePhotoUri
                                ?: throw Exception(
                                    "Please upload the license photo"
                                )

                        // Read original image
                        val originalImageBytes =
                            context.contentResolver
                                .openInputStream(photoUri)
                                ?.use { inputStream ->
                                    inputStream.readBytes()
                                }
                                ?: throw Exception(
                                    "Unable to read selected license photo."
                                )

                        // Compress image
                        val compressedImageBytes =
                            compressImage(
                                originalImageBytes
                            )

                        // Upload compressed image
                        val licensePhotoUrl =
                            licensePhotoRepository
                                .uploadLicensePhoto(
                                    imageBytes =
                                        compressedImageBytes
                                )

                        // Register Food Provider
                        authRepository.registerFoodProvider(
                            restaurantName =
                                restaurantName.trim(),

                            email =
                                email.trim().lowercase(),

                            phone =
                                phone.trim(),

                            address =
                                address.trim(),

                            licensePhoneUrl =
                                licensePhotoUrl,

                            password =
                                password,

                            latitude =
                                providerLatitude!!,

                            longitude =
                                providerLongitude!!
                        )
                    }
                }

                message =
                    "Registration successful"

                onRegisterSuccess()

            } catch (e: Exception) {

                message =
                    e.message ?: "Registration failed"

            } finally {

                isLoading = false
            }
        }
    }

    // ViewModel Factory
    companion object {

        fun Factory(
            authRepository: AuthRepository
        ): ViewModelProvider.Factory =
            viewModelFactory {

                initializer {

                    RegisterViewModel(
                        authRepository
                    )
                }
            }
    }
}