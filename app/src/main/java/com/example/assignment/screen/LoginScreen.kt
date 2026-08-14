package com.example.assignment.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.assignment.R
import com.example.assignment.model.User
import com.example.assignment.viewmodel.LoginUiState
import com.example.assignment.viewmodel.LoginViewModel
import io.github.jan.supabase.compose.auth.NativeSignInResult
import io.github.jan.supabase.compose.auth.rememberSignInWithGoogle

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onRegisterClick: () -> Unit,
    onLoginSuccess: (User) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

    val signInWithGoogle = rememberSignInWithGoogle(
        onResult = { result ->
            when (result) {
                is NativeSignInResult.Success -> {
                    val supabaseUser = result.data.user
                    if (supabaseUser != null) {
                        val userName = supabaseUser.userMetadata?.get("name") as? String ?: ""
                        val user = User(
                            email = supabaseUser.email ?: "",
                            name = userName,
                            password = "",
                            phone = "",
                            type = "FoodSaver",
                            restaurant = null,
                            address = null,
                            licenseUri = null
                        )
                        viewModel.loginWithGoogle(user)
                    } else {
                        dialogMessage = "Failed to get Google user info"
                        showDialog = true
                    }
                }
                is NativeSignInResult.Error -> {
                    dialogMessage = result.message ?: "Google login failed"
                    showDialog = true
                }
                is NativeSignInResult.ClosedByUser -> { }
                else -> {}
            }
        }
    )

    when (uiState) {
        is LoginUiState.Success -> {
            LaunchedEffect(Unit) { onLoginSuccess((uiState as LoginUiState.Success).user) }
        }
        is LoginUiState.Error -> {
            LaunchedEffect(Unit) {
                dialogMessage = (uiState as LoginUiState.Error).message
                showDialog = true
                viewModel.resetState()
            }
        }
        else -> {}
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(painterResource(id = R.drawable.logo), contentDescription = "Logo", modifier = Modifier.size(120.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text("Welcome Back", style = MaterialTheme.typography.headlineMedium, fontSize = 28.sp)
        Text("Sign in to continue saving food", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            enabled = uiState !is LoginUiState.Loading
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            enabled = uiState !is LoginUiState.Loading
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.loginWithEmail(email, password) },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            enabled = uiState !is LoginUiState.Loading
        ) {
            if (uiState is LoginUiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            } else {
                Text("Login", fontSize = 16.sp, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            Text("Don't have an account?", color = MaterialTheme.colorScheme.onSurfaceVariant)
            TextButton(onClick = onRegisterClick, enabled = uiState !is LoginUiState.Loading) {
                Text("Register", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { signInWithGoogle.startFlow() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color.LightGray),
            shape = RoundedCornerShape(8.dp),
            enabled = uiState !is LoginUiState.Loading
        ) {
            Text("Sign in with Google", color = Color.DarkGray, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Login Failed") },
                text = { Text(dialogMessage) },
                confirmButton = {
                    TextButton(onClick = { showDialog = false }) { Text("OK") }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}