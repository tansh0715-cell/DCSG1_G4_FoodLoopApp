package com.example.assignment.screen.profileModule

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.assignment.viewmodel.profile.ProfileViewModel

@Composable
fun EditProfileScreen(
    innerPadding: PaddingValues,
    navController: NavController,
    viewModel: ProfileViewModel
) {
    val context = LocalContext.current

    LaunchedEffect(viewModel.errorMessage, viewModel.successMessage) {
        viewModel.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
        viewModel.successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        when {
            viewModel.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            viewModel.errorMessage != null && !viewModel.isProfileLoaded -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = viewModel.errorMessage ?: "Unknown error",
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { viewModel.loadProfile() }) {
                        Text("Retry")
                    }
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = if (viewModel.role == "FOOD_PROVIDER") "Edit Provider Profile" else "Edit Profile",
                        style = MaterialTheme.typography.titleLarge
                    )

                    // Email - read only
                    OutlinedTextField(
                        value = viewModel.email,
                        onValueChange = {},
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false,
                        supportingText = { Text("Email cannot be changed") }
                    )

                    if (viewModel.role == "FOOD_SAVER") {
                        OutlinedTextField(
                            value = viewModel.name,
                            onValueChange = {
                                viewModel.name = it
                                if (viewModel.nameError != null) viewModel.clearFieldErrors()
                            },
                            label = { Text("Name") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = viewModel.nameError != null,
                            supportingText = {
                                viewModel.nameError?.let {
                                    Text(text = it, color = MaterialTheme.colorScheme.error)
                                }
                            },
                            singleLine = true
                        )
                    } else if (viewModel.role == "FOOD_PROVIDER") {
                        OutlinedTextField(
                            value = viewModel.restaurantName,
                            onValueChange = {
                                viewModel.restaurantName = it
                                if (viewModel.restaurantNameError != null) viewModel.clearFieldErrors()
                            },
                            label = { Text("Restaurant Name") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = viewModel.restaurantNameError != null,
                            supportingText = {
                                viewModel.restaurantNameError?.let {
                                    Text(text = it, color = MaterialTheme.colorScheme.error)
                                }
                            },
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = viewModel.address,
                            onValueChange = {
                                viewModel.address = it
                                if (viewModel.addressError != null) viewModel.clearFieldErrors()
                            },
                            label = { Text("Address") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = viewModel.addressError != null,
                            supportingText = {
                                viewModel.addressError?.let {
                                    Text(text = it, color = MaterialTheme.colorScheme.error)
                                }
                            },
                            minLines = 2
                        )

                        if (viewModel.licensePhotoUri.isNotBlank()) {
                            Text(
                                text = "License Photo (read-only)",
                                style = MaterialTheme.typography.labelMedium
                            )
                            AsyncImage(
                                model = viewModel.licensePhotoUri,
                                contentDescription = "License Photo",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = viewModel.phone,
                        onValueChange = {
                            viewModel.phone = it
                            if (viewModel.phoneError != null) viewModel.clearFieldErrors()
                        },
                        label = { Text("Phone") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = viewModel.phoneError != null,
                        supportingText = {
                            viewModel.phoneError?.let {
                                Text(text = it, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            viewModel.saveProfile {
                                navController.popBackStack()
                            }
                        },
                        enabled = !viewModel.isSaving && viewModel.isProfileLoaded,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (viewModel.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .height(20.dp)
                                    .padding(end = 8.dp),
                                strokeWidth = 2.dp
                            )
                            Text("Saving...")
                        } else {
                            Text("Save Changes")
                        }
                    }

                    if (viewModel.errorMessage != null) {
                        Text(
                            text = viewModel.errorMessage ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}
