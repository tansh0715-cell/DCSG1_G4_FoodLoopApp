package com.example.assignment.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.assignment.R
import com.example.assignment.viewmodel.RegisterProviderViewModel
import com.example.assignment.viewmodel.RegisterProviderUiState

@Composable
fun RegisterProviderScreen(
    viewModel: RegisterProviderViewModel,
    onBackClick: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    var restaurant by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> imageUri = uri }
    )

    when (uiState) {
        is RegisterProviderUiState.Success -> { LaunchedEffect(Unit) { onRegisterSuccess() } }
        is RegisterProviderUiState.Error -> {
            LaunchedEffect(Unit) {
                dialogMessage = (uiState as RegisterProviderUiState.Error).message
                showDialog = true
                viewModel.resetState()
            }
        }
        else -> {}
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(56.dp)) {
            IconButton(onClick = onBackClick, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(painterResource(id = android.R.drawable.ic_menu_revert), contentDescription = "Back")
            }
            Text("Create Account", fontSize = 22.sp, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.align(Alignment.Center), textAlign = TextAlign.Center)
        }
        Spacer(modifier = Modifier.height(8.dp))

        Image(painterResource(id = R.drawable.foodprovider), contentDescription = "Food Provider", modifier = Modifier.size(80.dp))
        Text("Food Provider", color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = restaurant, onValueChange = { restaurant = it }, label = { Text("Restaurant Name") }, singleLine = true, modifier = Modifier.fillMaxWidth(), enabled = uiState !is RegisterProviderUiState.Loading)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, singleLine = true, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), enabled = uiState !is RegisterProviderUiState.Loading)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone Number") }, singleLine = true, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), enabled = uiState !is RegisterProviderUiState.Loading)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address") }, singleLine = true, modifier = Modifier.fillMaxWidth(), enabled = uiState !is RegisterProviderUiState.Loading)
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth().height(150.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                .background(Color.White, RoundedCornerShape(12.dp))
                .clickable { pickImageLauncher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null) {
                AsyncImage(model = imageUri, contentDescription = "License", modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
            } else {
                Text("Upload your license", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, singleLine = true, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), enabled = uiState !is RegisterProviderUiState.Loading)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = confirmPassword, onValueChange = { confirmPassword = it }, label = { Text("Confirm Password") }, singleLine = true, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), enabled = uiState !is RegisterProviderUiState.Loading)
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.register(restaurant, email, phone, address, password, confirmPassword, imageUri) },
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState !is RegisterProviderUiState.Loading
        ) {
            if (uiState is RegisterProviderUiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            } else {
                Text("Register", fontSize = 16.sp, color = Color.White)
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Registration Failed") },
                text = { Text(dialogMessage) },
                confirmButton = {
                    TextButton(onClick = { showDialog = false }) { Text("OK") }
                }
            )
        }
    }
}