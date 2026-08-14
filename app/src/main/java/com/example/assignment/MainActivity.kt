package com.example.assignment

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.assignment.components.AppNavigationBar
import com.example.assignment.nav.AppNavGraph

@Composable
fun MainApp() {
    val navController = rememberNavController()

    val isConsumer = true
    val username = "Test User"
    val role = "FoodSaver"

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
            isConsumer = isConsumer
        )
    }
}