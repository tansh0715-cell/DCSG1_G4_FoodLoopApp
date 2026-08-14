package com.example.assignment.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.assignment.R
import com.example.assignment.viewmodel.RegisterSaverViewModel
import com.example.assignment.viewmodel.RegisterSaverUiState

@Composable
fun RegisterSaverScreen(
    viewModel: RegisterSaverViewModel,
    onBackClick: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

    when (uiState) {
        is RegisterSaverUiState.Success -> { LaunchedEffect(Unit) { onRegisterSuccess() } }
        is RegisterSaverUiState.Error -> {
            LaunchedEffect(Unit) {
                dialogMessage = (uiState as RegisterSaverUiState.Error).message
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

        Image(painterResource(id = R.drawable.foodsaver), contentDescription = "Food Saver", modifier = Modifier.size(80.dp))
        Text("Food Saver", color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") }, singleLine = true, modifier = Modifier.fillMaxWidth(), enabled = uiState !is RegisterSaverUiState.Loading)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, singleLine = true, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), enabled = uiState !is RegisterSaverUiState.Loading)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone Number") }, singleLine = true, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), enabled = uiState !is RegisterSaverUiState.Loading)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, singleLine = true, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), enabled = uiState !is RegisterSaverUiState.Loading)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = confirmPassword, onValueChange = { confirmPassword = it }, label = { Text("Confirm Password") }, singleLine = true, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), enabled = uiState !is RegisterSaverUiState.Loading)
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.register(name, email, phone, password, confirmPassword) },
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState !is RegisterSaverUiState.Loading
        ) {
            if (uiState is RegisterSaverUiState.Loading) {
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