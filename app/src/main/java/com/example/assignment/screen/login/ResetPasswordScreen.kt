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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.assignment.data.repository.AuthRepository
import kotlinx.coroutines.launch


@Composable
fun ResetPasswordScreen(
    authRepository: AuthRepository,
    onPasswordUpdated: () -> Unit,
    onBackToLogin: () -> Unit
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Reset Password",
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("New Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {

                if (newPassword.isBlank()) {
                    message = "Please enter a new password"
                    return@Button
                }

                if (newPassword != confirmPassword) {
                    message = "Passwords do not match"
                    return@Button
                }

                scope.launch {
                    isLoading = true
                    try {
                        authRepository.updatePassword(newPassword)
                        message = "Password updated successfully"
                        kotlinx.coroutines.delay(1000)
                        onPasswordUpdated()
                    } catch (e: Exception) {
                        message =
                            e.message ?: "Failed to update password"
                    } finally {
                        isLoading = false
                    }
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Update Password")
        }

        if (message.isNotBlank()) {
            Text(
                text = message,
                color = if (message.contains("Failed") || message.contains("Incorrect"))
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.primary
            )
        }

        TextButton(onClick = onBackToLogin) {
            Text("Back to Login")
        }
    }
}