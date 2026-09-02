package com.example.assignment.screen.register

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.assignment.R
import com.example.assignment.data.repository.AuthRepository
import com.example.assignment.location.LocationTracker
import com.example.assignment.viewmodel.register.RegisterViewModel

@Composable
fun RegisterScreen(
    accountType: String,
    authRepository: AuthRepository,
    onRegisterSuccess: () -> Unit,
    onBackToChoose: () -> Unit,
    viewModel: RegisterViewModel =
        viewModel(
            factory = RegisterViewModel.Factory(authRepository)
        )
) {
    val context = LocalContext.current
    val locationTracker = remember {
        LocationTracker(
            context.applicationContext
        )
    }
    val locationPermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val granted =
                permissions[ Manifest.permission.ACCESS_FINE_LOCATION ] == true ||
                        permissions[ Manifest.permission.ACCESS_COARSE_LOCATION ] == true
            if (granted) {
                locationTracker.start { location ->
                    viewModel.setProviderLocation(
                        latitude = location.latitude,
                        longitude = location.longitude
                    )
                }
            }
        }

    DisposableEffect(accountType) {
        if (accountType == "FOOD_PROVIDER") {
            val hasPermission =
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED

            if (hasPermission) {
                locationTracker.start { location ->
                    viewModel.setProviderLocation(
                        latitude = location.latitude,
                        longitude = location.longitude
                    )
                }
            } else {
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }

        onDispose {
            locationTracker.stop()
        }
    }

    val photoPickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->
            viewModel.licensePhotoUri = uri
            if (uri != null) {
                viewModel.clearLicensePhotoError()
            }
        }

    // General Message
    LaunchedEffect(viewModel.message) {
        viewModel.message?.let {
            Toast.makeText(
                context,
                it,
                Toast.LENGTH_SHORT
            ).show()
            viewModel.clearMessage()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll( rememberScrollState() ),
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackToChoose
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Text(
                text = "Create Account",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f),
                textAlign =  TextAlign.Center
            )
            Spacer(modifier = Modifier.width(48.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (accountType == "FOOD_SAVER") {
                Image(
                    painter = painterResource(id = R.drawable.foodsaver),
                    contentDescription = "Food Saver",
                    modifier = Modifier.size(80.dp)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.foodprovider),
                    contentDescription = "Food Provider",
                    modifier = Modifier.size(80.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text =
                    if (
                        accountType == "FOOD_SAVER"
                    ) {
                        "Food Saver"
                    } else {
                        "Food Provider"
                    },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth(),
                textAlign =TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        if (accountType == "FOOD_SAVER") {
            OutlinedTextField(
                value = viewModel.name,
                onValueChange = {
                    viewModel.name = it
                    viewModel.clearNameError()
                },
                label = { Text("Name") },
                isError = viewModel.nameError != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    errorTextColor = Color.Black
                ),
                modifier = Modifier.fillMaxWidth()
            )
            viewModel.nameError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = viewModel.email,
                onValueChange = {
                    viewModel.email = it
                    viewModel.clearEmailError()
                },
                label = { Text("Email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = viewModel.emailError != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    errorTextColor = Color.Black
                ),
                modifier = Modifier.fillMaxWidth()
            )
            viewModel.emailError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = viewModel.phone,
                onValueChange = {
                    viewModel.phone = it
                    viewModel.clearPhoneError()
                },
                label = { Text("Phone") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = viewModel.phoneError != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    errorTextColor = Color.Black
                ),
                modifier = Modifier.fillMaxWidth()
            )
            viewModel.phoneError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = viewModel.password,
                onValueChange = {
                    viewModel.password = it
                    viewModel.clearPasswordError()
                },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                isError = viewModel.passwordError != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    errorTextColor = Color.Black
                ),
                modifier = Modifier.fillMaxWidth()
            )

            viewModel.passwordError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = viewModel.confirmPassword,
                onValueChange = {
                    viewModel.confirmPassword = it
                    viewModel.clearConfirmPasswordError()
                },
                label = { Text("Confirm Password") },
                visualTransformation = PasswordVisualTransformation(),
                isError = viewModel.confirmPasswordError != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    errorTextColor = Color.Black
                ),
                modifier = Modifier.fillMaxWidth()
            )
            viewModel.confirmPasswordError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp )
                )
            }
            Spacer(modifier = Modifier.height(20.dp))

        } else {
            OutlinedTextField(
                value = viewModel.restaurantName,
                onValueChange = {
                    viewModel.restaurantName = it
                    viewModel.clearRestaurantNameError()
                },
                label = { Text("Restaurant Name") },
                isError = viewModel.restaurantNameError != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    errorTextColor = Color.Black
                ),
                modifier = Modifier.fillMaxWidth()
            )
            viewModel.restaurantNameError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = viewModel.email,
                onValueChange = {
                    viewModel.email = it
                    viewModel.clearEmailError()
                },
                label = { Text("Email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = viewModel.emailError != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    errorTextColor = Color.Black
                ),
                modifier = Modifier.fillMaxWidth()
            )
            viewModel.emailError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp,  top = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = viewModel.phone,
                onValueChange = {
                    viewModel.phone = it
                    viewModel.clearPhoneError()
                },
                label = { Text("Phone") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = viewModel.phoneError != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    errorTextColor = Color.Black
                ),
                modifier = Modifier.fillMaxWidth()
            )
            viewModel.phoneError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value =viewModel.address,
                onValueChange = {
                    viewModel.address = it
                    viewModel.clearAddressError()
                },
                label = { Text("Address")},
                isError = viewModel.addressError != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    errorTextColor = Color.Black
                ),
                modifier = Modifier.fillMaxWidth()
            )
            viewModel.addressError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .border(
                            width = 1.dp,
                            color =
                                if (viewModel.licensePhotoError != null) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    Color.Gray
                                },
                            shape =RoundedCornerShape(12.dp)
                        )
                        .clickable { photoPickerLauncher .launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (viewModel.licensePhotoUri != null) {
                    AsyncImage(
                        model = viewModel.licensePhotoUri,
                        contentDescription = "License Photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = "Upload your license",
                        color = Color.LightGray,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            viewModel.licensePhotoError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = viewModel.password,
                onValueChange = {
                    viewModel.password = it
                    viewModel.clearPasswordError()
                },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                isError = viewModel.passwordError != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    errorTextColor = Color.Black
                ),
                modifier = Modifier.fillMaxWidth()
            )
            viewModel.passwordError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))


            OutlinedTextField(
                value = viewModel.confirmPassword,
                onValueChange = {
                    viewModel.confirmPassword = it
                    viewModel.clearConfirmPasswordError()
                },
                label = { Text("Confirm Password") },
                visualTransformation =PasswordVisualTransformation(),
                isError = viewModel.confirmPasswordError != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    errorTextColor = Color.Black
                ),
                modifier = Modifier.fillMaxWidth()
            )

            viewModel.confirmPasswordError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
        viewModel.locationError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
            )
        }

        Button(
            onClick = {
                viewModel.register(
                    accountType = accountType,
                    context = context,
                    onRegisterSuccess = onRegisterSuccess
                )
            },
            enabled = !viewModel.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}