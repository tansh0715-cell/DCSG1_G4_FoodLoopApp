package com.example.assignment.screen.login


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.assignment.data.repository.AuthRepository
import kotlinx.coroutines.launch

@Composable
fun ForgotPasswordScreen (
    authRepository: AuthRepository,
    onBackToLogin: () -> Unit
){
    var email by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun isValidEmail(email: String): Boolean {
        val regex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        return email.matches(regex)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Forgot Password",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = "Enter your email to receive a reset link",
            style = MaterialTheme.typography.bodyMedium
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(0.9f)
        )

        Button(

            onClick = {

                if (email.isBlank()) {
                    message = "Please enter your email"
                    return@Button
                }

                if (!isValidEmail(email)) {
                    message = "Please enter a valid email address"
                    return@Button
                }

                scope.launch {
                    isLoading = true
                    try {
                        authRepository.resetPassword(email)
                        message = "Reset link sent to your email"
                    } catch (e: Exception) {
                        message = e.message ?: "Failed to send reset link"
                    } finally {
                        isLoading = false
                    }
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Text("Send Reset Link")
        }

        if (message.isNotBlank()) {
            Text(
                text = message,
                color = if (message.contains("Failed")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
        }

        TextButton (
            onClick = onBackToLogin
        ) {
            Text("Back to Login")
        }
    }
}