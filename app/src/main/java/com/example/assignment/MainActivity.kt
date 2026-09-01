package com.example.assignment


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.assignment.data.UserPreferencesManager
import com.example.assignment.data.repository.AuthRepository
import com.example.assignment.data.supabase.supabase
import com.example.assignment.nav.AppNavigation
import com.example.assignment.ui.theme.AssignmentTheme
import io.github.jan.supabase.auth.handleDeeplinks
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.assignment.notification.NotificationWorkerScheduler

class MainActivity : ComponentActivity() {

    private lateinit var authRepository: AuthRepository
    private var navController: NavHostController? = null
    private lateinit var userPreferencesManager: UserPreferencesManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    100
                )
            }
        }
        NotificationWorkerScheduler.start(this)

        authRepository = AuthRepository()
        userPreferencesManager = UserPreferencesManager(this)

        setContent {
            val navController = rememberNavController()

            val userRoleFlow = userPreferencesManager.getUserRoleFlow()
            val userRole by userRoleFlow.collectAsState(initial = null)

            val startDestination = when (userRole) {
                "FOOD_SAVER" -> "HOME"
                "FOOD_PROVIDER" -> "PROVIDER_HOME"
                else -> "LOGIN"
            }

            handleAuthDeepLink(intent, navController)

            AssignmentTheme {
                AppNavigation(
                    navController = navController,
                    authRepository = authRepository,
                    startDestination = startDestination
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleAuthDeepLink(intent, navController)
    }

    private fun handleAuthDeepLink(
        intent: Intent?,
        navController: NavHostController?
    ) {
        val uri = intent?.data ?: return
        Log.d("DeepLink", "URI = $uri")
        Log.d("DeepLink", "Host = ${uri.host}")
        Log.d("DeepLink", "Path = ${uri.path}")

        supabase.handleDeeplinks(intent)

        when (uri.host) {
            "reset-callback" -> {
                Log.d("DeepLink", "🔄 Navigating to RESET_PASSWORD")
                Handler(Looper.getMainLooper()).postDelayed({
                    navController?.navigate("RESET_PASSWORD") {
                        popUpTo("LOGIN") { inclusive = false }
                        launchSingleTop = true
                    }
                }, 500)
            }
            "login-callback" -> {
                Log.d("DeepLink", "Legacy login-callback")
            }
            else -> {
                Log.d("DeepLink", "Unknown host: ${uri.host}")
            }
        }
    }
}