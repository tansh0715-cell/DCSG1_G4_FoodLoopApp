package com.example.assignment


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.assignment.data.repository.AuthRepository
import com.example.assignment.data.supabase.supabase
import com.example.assignment.nav.AppNavigation
import com.example.assignment.ui.theme.AssignmentTheme
import io.github.jan.supabase.auth.handleDeeplinks

class MainActivity : ComponentActivity() {

    private lateinit var authRepository: AuthRepository
    private var navController: NavHostController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authRepository = AuthRepository()
        setContent {
            val navHostController = rememberNavController()
            navController = navHostController

            handleAuthDeepLink(intent, navHostController)
            AssignmentTheme() {
                AppNavigation(
                    navController = navHostController,
                    authRepository = authRepository
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