package com.example.assignment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.assignment.data.UserPreferencesManager
import com.example.assignment.data.repository.AuthRepository
import com.example.assignment.data.supabase.supabase
import com.example.assignment.nav.AppNavigation
import com.example.assignment.notification.NotificationWorkerScheduler
import com.example.assignment.ui.theme.AssignmentTheme
import io.github.jan.supabase.auth.handleDeeplinks

class MainActivity : ComponentActivity() {

    private lateinit var authRepository: AuthRepository
    private var navController: NavHostController? = null
    private lateinit var userPreferencesManager: UserPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // =========================
        // NOTIFICATION PERMISSION
        // =========================
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

        // =========================
        // START NOTIFICATION WORKER
        // =========================
        NotificationWorkerScheduler.start(this)

        // =========================
        // INITIALIZE
        // =========================
        authRepository = AuthRepository()
        userPreferencesManager = UserPreferencesManager(this)

        setContent {

            val navController = rememberNavController()

            // Save NavController for onNewIntent()
            this@MainActivity.navController = navController

            // =========================
            // GET USER ROLE
            // =========================
            val userRoleFlow = userPreferencesManager.getUserRoleFlow()
            val userRole by userRoleFlow.collectAsState(initial = null)

            val startDestination = when (userRole) {
                "FOOD_SAVER" -> "HOME"
                "FOOD_PROVIDER" -> "PROVIDER_HOME"
                else -> "LOGIN"
            }

            // =========================
            // APP THEME
            // =========================
            AssignmentTheme {

                // =========================
                // NAVIGATION
                // =========================
                AppNavigation(
                    navController = navController,
                    authRepository = authRepository,
                    startDestination = startDestination
                )

                // =========================
                // HANDLE INITIAL DEEP LINK
                // =========================
                LaunchedEffect(intent) {
                    handleAuthDeepLink(
                        intent = intent,
                        navController = navController
                    )
                }
            }
        }
    }

    // =========================
    // HANDLE NEW DEEP LINK
    // =========================
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        setIntent(intent)

        navController?.let {
            handleAuthDeepLink(
                intent = intent,
                navController = it
            )
        }
    }

    // =========================
    // HANDLE SUPABASE AUTH LINK
    // =========================
    private fun handleAuthDeepLink(
        intent: Intent?,
        navController: NavHostController?
    ) {
        val uri = intent?.data ?: return

        // Only handle our authentication callback
        if (uri.scheme != "com.example.assignment") {
            return
        }

        if (uri.host != "auth-callback") {
            return
        }

        try {

            // Let Supabase handle the authentication session
            supabase.handleDeeplinks(intent)

            // Get the URL fragment
            val fragment = uri.fragment ?: ""

            // =========================
            // PASSWORD RECOVERY
            // =========================
            if (fragment.contains("type=recovery")) {

                navController?.navigate("RESET_PASSWORD") {
                    launchSingleTop = true
                }
            }

        } catch (e: Exception) {

            Toast.makeText(
                this,
                "The authentication link is invalid or expired.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}