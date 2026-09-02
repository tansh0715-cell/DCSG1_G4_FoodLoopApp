package com.example.assignment.screen.profileModule

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.assignment.viewmodel.profile.ChangePasswordViewModel

@Composable
fun ChangePasswordScreen(
    innerPadding: PaddingValues,
    navController: NavController,
    viewModel: ChangePasswordViewModel
) {
    val context = LocalContext.current

    LaunchedEffect(viewModel.errorMessage, viewModel.successMessage) {
        viewModel.successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Enter your new password.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSecondary
        )

        OutlinedTextField(
            value = viewModel.currentPassword,
            onValueChange = {
                viewModel.currentPassword = it
                if (viewModel.currentPasswordError != null) viewModel.clearErrors()
            },
            label = { Text("Current Password *") },
            placeholder = { Text("Current password (required)") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            isError = viewModel.currentPasswordError != null,
            supportingText = {
                viewModel.currentPasswordError?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                } ?: Text(text = "Required to verify identity", color = MaterialTheme.colorScheme.onSecondary)
            },
            singleLine = true
        )

        OutlinedTextField(
            value = viewModel.newPassword,
            onValueChange = {
                viewModel.newPassword = it
                if (viewModel.newPasswordError != null) viewModel.clearErrors()
            },
            label = { Text("New Password") },
            placeholder = { Text("At least 6 characters") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            isError = viewModel.newPasswordError != null,
            supportingText = {
                viewModel.newPasswordError?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
            },
            singleLine = true
        )

        OutlinedTextField(
            value = viewModel.confirmPassword,
            onValueChange = {
                viewModel.confirmPassword = it
                if (viewModel.confirmPasswordError != null) viewModel.clearErrors()
            },
            label = { Text("Confirm New Password *") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            isError = viewModel.confirmPasswordError != null || viewModel.isConfirmMismatch,
            supportingText = {
                when {
                    viewModel.confirmPasswordError != null -> Text(text = viewModel.confirmPasswordError!!, color = MaterialTheme.colorScheme.error)
                    viewModel.isConfirmMismatch -> Text(text = "Passwords do not match", color = MaterialTheme.colorScheme.error)
                    else -> {}
                }
            },
            singleLine = true
        )

        if (viewModel.errorMessage != null) {
            Text(
                text = viewModel.errorMessage ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                viewModel.changePassword {
                    navController.popBackStack()
                }
            },
            enabled = !viewModel.isSaving && !viewModel.isConfirmMismatch && viewModel.newPassword.isNotBlank() && viewModel.confirmPassword.isNotBlank() && viewModel.currentPassword.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (viewModel.isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .height(20.dp)
                        .padding(end = 8.dp),
                    strokeWidth = 2.dp
                )
                Text("Updating...",
                    color = MaterialTheme.colorScheme.background)
            } else {
                Text("Update Password",
                    color = MaterialTheme.colorScheme.background)
            }
        }
    }
}
