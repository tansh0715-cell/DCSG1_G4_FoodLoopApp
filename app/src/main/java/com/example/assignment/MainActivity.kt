package com.example.assignment


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Scaffold
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

class MainActivity : ComponentActivity() {

    private lateinit var authRepository: AuthRepository
    private var navController: NavHostController? = null
    private lateinit var userPreferencesManager: UserPreferencesManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 初始化依赖
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

    private fun handleAuthDeepLink(intent: Intent?, navController: NavHostController?) {
        val uri = intent?.data ?: return
        Log.d("DeepLink", "URI = $uri")
        Log.d("DeepLink", "Fragment = ${uri.fragment}")
        supabase.handleDeeplinks(intent)
        val fragment = uri.fragment.orEmpty()
        if (fragment.contains("type=recovery")) {
            navController?.navigate("RESET_PASSWORD")
        }
    }
}