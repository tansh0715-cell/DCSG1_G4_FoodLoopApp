package com.example.assignment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.assignment.data.UserPreferences
import com.example.assignment.model.User
import com.example.assignment.screen.RegisterProviderScreen
import com.example.assignment.ui.theme.AssignmentTheme

class RegisterProvider : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UserPreferences.init(applicationContext)
        setContent {
            AssignmentTheme {
                var showDialog by remember { mutableStateOf(false) }
                var dialogMessage by remember { mutableStateOf("") }
                var shouldNavigate by remember { mutableStateOf(false) }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    RegisterProviderScreen(
                        onBackClick = { finish() },
                        onRegisterClick = { restaurant, email, phone, address, password, confirmPassword, imageUri ->
                            val result = performRegister(restaurant, email, phone, address, password, confirmPassword, imageUri)

                            if (result != null) {
                                dialogMessage = result
                                showDialog = true
                            } else {
                                shouldNavigate = true
                            }
                        }
                    )
                }

                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text(text = "Registration Failed") },
                        text = { Text(text = dialogMessage) },
                        confirmButton = {
                            TextButton(onClick = { showDialog = false }) {
                                Text(text = "OK")
                            }
                        }
                    )
                }

                LaunchedEffect(shouldNavigate) {
                    if (shouldNavigate) {
                        startActivity(Intent(this@RegisterProvider, Login::class.java))
                        finish()
                    }
                }

            }
        }
    }

    private fun performRegister(restaurant: String, email: String, phone: String, address: String, password: String, confirmPassword: String, imageUri: Uri?): String? {
        val trimmedRestaurant = restaurant.trim()
        val trimmedEmail = email.trim()
        val trimmedPhone = phone.trim()
        val trimmedAddress = address.trim()
        val trimmedPassword = password.trim()
        val trimmedConfirmPassword = confirmPassword.trim()


        if (trimmedRestaurant.isEmpty() || trimmedEmail.isEmpty() || trimmedPhone.isEmpty() || trimmedAddress.isEmpty() || trimmedPassword.isEmpty() || trimmedConfirmPassword.isEmpty()) {
            return "Please fill all fields"
        }
        if (trimmedPassword != trimmedConfirmPassword) {
            return "Passwords do not match"
        }
        if (imageUri == null) {
            return "Please upload a license photo"
        }

        if (UserPreferences.checkUserExists(trimmedEmail)) {
            return "Email already registered"
        }

        val user = User(
            name = trimmedRestaurant,
            email = trimmedEmail,
            password = trimmedPassword,
            phone = trimmedPhone,
            type = "FoodProvider",
            restaurant = trimmedRestaurant,
            address = trimmedAddress
            )
        UserPreferences.saveUser(user)

        UserPreferences.saveLicense(trimmedEmail, imageUri.toString())

        return null
    }
}
