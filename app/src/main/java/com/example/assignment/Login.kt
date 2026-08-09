package com.example.assignment

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.assignment.data.UserPreferences
import com.example.assignment.model.User
import com.example.assignment.screen.LoginScreen
import com.example.assignment.ui.theme.AssignmentTheme

class Login : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UserPreferences.init(applicationContext)

        setContent {
            AssignmentTheme {
                var showDialog by remember { mutableStateOf(false) }
                var dialogMessage by remember { mutableStateOf("") }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    LoginScreen(
                        onLoginClick = { email, password ->
                            performLogin(email, password) { success, message, user ->
                                if (success && user != null) {
                                    // Login successful → go to MainActivity
                                    val intent = Intent(this@Login, MainActivity::class.java)
                                    intent.putExtra("role", user.type)
                                    intent.putExtra("username", user.name)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    // Login failed → show error dialog
                                    dialogMessage = message ?: "Login failed"
                                    showDialog = true
                                }
                            }
                        },
                        onRegisterClick = {
                            startActivity(Intent(this@Login, RegisterType::class.java))
                        }
                    )
                }

                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("Login Failed") },
                        text = { Text(dialogMessage) },
                        confirmButton = {
                            TextButton(onClick = { showDialog = false }) {
                                Text("OK")
                            }
                        }
                    )
                }
            }
        }
    }

    private fun performLogin(
        email: String,
        password: String,
        onResult: (Boolean, String?, User?) -> Unit
    ) {
        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()

        // Empty field check
        if (trimmedEmail.isEmpty() || trimmedPassword.isEmpty()) {
            onResult(false, "Please enter email and password", null)
            return
        }

        // Debug logs
        Log.d("LoginDebug", "Input email: '$trimmedEmail'")
        Log.d("LoginDebug", "Input password: '$trimmedPassword'")

        // Fetch user from SharedPreferences
        val user = UserPreferences.getUser(trimmedEmail)

        if (user != null) {
            Log.d("LoginDebug", "User found: ${user.name}")
            Log.d("LoginDebug", "Stored password: '${user.password}'")
            Log.d("LoginDebug", "Password match: ${user.password == trimmedPassword}")
        } else {
            Log.d("LoginDebug", "User not found: $trimmedEmail")
        }

        // Validate credentials
        if (user != null && user.password == trimmedPassword) {
            Toast.makeText(this, "Welcome ${user.name}!", Toast.LENGTH_SHORT).show()
            onResult(true, null, user)
        } else {
            onResult(false, "Invalid email or password", null)
        }
    }
}