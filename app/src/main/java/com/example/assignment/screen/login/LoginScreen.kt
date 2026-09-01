package com.example.assignment.screen.login

import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.assignment.R
import com.example.assignment.data.UserPreferencesManager
import com.example.assignment.data.repository.AuthRepository
import com.example.assignment.data.supabase.supabase
import com.example.assignment.model.FoodProvider
import com.example.assignment.model.FoodSaver
import com.example.assignment.viewmodel.LoginViewModel
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

@Composable
fun LoginScreen(
    onFoodSaverLogin: () -> Unit,
    onFoodProviderLogin: () -> Unit,
    onRegister: () -> Unit,
    onForgotPassword: () -> Unit,
    authRepository: AuthRepository
) {
    val context = LocalContext.current
    val userPreferencesManager = remember { UserPreferencesManager(context) }

    val loginViewModel: LoginViewModel = viewModel(
        factory = LoginViewModel.Factory(
            authRepository = authRepository,
            userPreferencesManager = userPreferencesManager
        )
    )

    LaunchedEffect(loginViewModel.message) {
        loginViewModel.message?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            loginViewModel.clearMessage()
        }
    }

    var isCheckingSession by remember { mutableStateOf(true) }

    // 检查是否已登录
    LaunchedEffect(Unit) {
        val currentUser = supabase.auth.currentUserOrNull()
        if (currentUser != null) {
            try {
                val userId = currentUser.id

                val saver = supabase.from("food_savers")
                    .select {
                        filter { eq("user_id", userId) }
                    }
                    .decodeSingleOrNull<FoodSaver>()

                if (saver != null) {
                    onFoodSaverLogin()
                    isCheckingSession = false
                    return@LaunchedEffect
                }

                val provider = supabase.from("food_providers")
                    .select {
                        filter { eq("user_id", userId) }
                    }
                    .decodeSingleOrNull<FoodProvider>()

                if (provider != null) {
                    onFoodProviderLogin()
                    isCheckingSession = false
                    return@LaunchedEffect
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        isCheckingSession = false
    }

    // ✅ 检查会话时显示加载圈（而不是 "Checking session..."）
    if (isCheckingSession) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = Color(0xFF2E7D32),
                modifier = Modifier.size(48.dp)
            )
        }
        return
    }

    // ===== 登录表单 =====
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "FoodLoop Logo",
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .padding(bottom = 8.dp),
            contentScale = ContentScale.Fit
        )

        Text(
            text = "Welcome Back",
            style = MaterialTheme.typography.headlineLarge
                .copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Text(
            text = "Sign in to continue saving food",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.LightGray,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = loginViewModel.email,
            onValueChange = { loginViewModel.email = it },
            label = { Text("Email") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedLabelColor = Color.LightGray,
                unfocusedLabelColor = Color.LightGray
            )
        )
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = loginViewModel.password,
            onValueChange = { loginViewModel.password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedLabelColor = Color.LightGray,
                unfocusedLabelColor = Color.LightGray
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = { onForgotPassword() }) {
                Text(
                    text = "Forgot?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF2E7D32)
                )
            }
        }

        Button(
            onClick = {
                loginViewModel.login { accountType ->
                    if (accountType == "FOOD_SAVER") {
                        onFoodSaverLogin()
                    } else {
                        onFoodProviderLogin()
                    }
                }
            },
            enabled = !loginViewModel.isLoading,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2E7D32),
                contentColor = Color(0xFF2E7D32),
                disabledContainerColor = Color(0xFF2E7D32),
                disabledContentColor = Color(0xFF2E7D32)
            )
        ) {
            Text(
                text = "Login",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Don't have an account?",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.LightGray
            )
            TextButton(onClick = { onRegister() }) {
                Text(
                    text = "Register",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF2E7D32)
                )
            }
        }
    }
}