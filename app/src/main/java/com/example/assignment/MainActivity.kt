package com.example.assignment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.assignment.components.AppNavigationBar
import com.example.assignment.nav.AppNavGraph
import com.example.assignment.ui.theme.AssignmentTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val role = intent.getStringExtra("role") ?: "FoodSaver"
        val username = intent.getStringExtra("username") ?: "Test User"

        setContent {
            AssignmentTheme {
                MainApp(role = role, username = username)
            }
        }
    }
}

@Composable
fun MainApp(role: String, username: String) {
    val navController = rememberNavController()
    val isConsumer = role == "FoodSaver"

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            AppNavigationBar(
                navController = navController,
                isConsumer = isConsumer
            )
        }
    ) { innerPadding ->
        AppNavGraph(
            navController = navController,
            innerPadding = innerPadding,
            isConsumer = isConsumer,
            username = username,
            role = role
        )
    }
}