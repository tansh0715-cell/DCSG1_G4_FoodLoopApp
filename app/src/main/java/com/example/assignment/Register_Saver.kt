package com.example.assignment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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
import com.example.assignment.screen.RegisterSaverScreen
import com.example.assignment.ui.theme.AssignmentTheme

class RegisterSaver : ComponentActivity() {

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
                    RegisterSaverScreen(
                        onBackClick = { finish() },
                        onRegisterClick = { name, email, phone, password, confirmPassword ->
                            val result = performRegister(name, email, phone, password, confirmPassword)

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
                        startActivity(Intent(this@RegisterSaver, Login::class.java))
                        finish()
                    }
                }
            }
        }
    }

    private fun performRegister(name: String, email: String, phone: String, password: String, confirmPassword: String): String? {
        val trimmedName = name.trim()
        val trimmedEmail = email.trim()
        val trimmedPhone = phone.trim()
        val trimmedPassword = password.trim()
        val trimmedConfirmPassword = confirmPassword.trim()

        if (trimmedName.isEmpty() || trimmedEmail.isEmpty() || trimmedPhone.isEmpty() || trimmedPassword.isEmpty() || trimmedConfirmPassword.isEmpty()) {
            return "Please fill all fields"
        }

        if (trimmedPassword != trimmedConfirmPassword) {
            return "Passwords do not match"
        }

        if (UserPreferences.checkUserExists(trimmedEmail)) {
            return "Email already registered"
        }

        val user = User(
            name = trimmedName,
            email = trimmedEmail,
            password = trimmedPassword,
            phone = trimmedPhone,
            type = "FoodSaver"
        )
        UserPreferences.saveUser(user)

        return null
    }
}
