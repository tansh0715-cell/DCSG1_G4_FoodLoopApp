package com.example.assignment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.assignment.components.AppNavigationBar
import com.example.assignment.data.foodList
import com.example.assignment.data.reservationsList
import com.example.assignment.screen.AddFoodScreen
import com.example.assignment.screen.FoodDetailScreen
import com.example.assignment.screen.HomeScreen
import com.example.assignment.screen.InventoryScreen
import com.example.assignment.screen.NotificationScreen
import com.example.assignment.screen.OrderDetailScreen
import com.example.assignment.screen.OrderScreen
import com.example.assignment.screen.ProfileScreen
import com.example.assignment.screen.ProviderHomeScreen
import com.example.assignment.screen.ProviderNotificationScreen
import com.example.assignment.screen.ProviderOrderScreen
import com.example.assignment.screen.RestaurantDetailScreen
import com.example.assignment.ui.theme.AssignmentTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AssignmentTheme {
                MainApp()

            }
        }
    }

}

@Composable
fun MainApp() {
    val navController = rememberNavController()
    val isConsumer = true

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { AppNavigationBar(navController, isConsumer) })
    { innerPadding ->
        //screen content
        NavHost(
            navController = navController,
            startDestination = "home"
        ) {
            composable("home") {
                if (isConsumer) {
                    HomeScreen(innerPadding = innerPadding, navController = navController)
                } else {
                    ProviderHomeScreen(innerPadding = innerPadding, navController = navController)
                }
            }
            composable("restaurant_detail") {
                RestaurantDetailScreen(
                    innerPadding = innerPadding,
                    navController = navController
                )
            }
            composable("food_detail/{foodIndex}") { backStackEntry ->

                val indexString = backStackEntry.arguments?.getString("foodIndex")
                val index = indexString?.toIntOrNull() ?: 0

                val selectedFood = foodList[index]

                FoodDetailScreen(
                    innerPadding = innerPadding,
                    food = selectedFood,
                    onBlackClick = { navController.popBackStack() } // back to previous page
                )
            }
            composable("add") {
                AddFoodScreen(innerPadding = innerPadding, navController = navController)
            }
            composable("order") {
                if (isConsumer) {
                    OrderScreen(innerPadding = innerPadding, navController = navController)
                } else {
                    ProviderOrderScreen(innerPadding = innerPadding, navController = navController)
                }
            }

            composable("order_detail/{orderIndex}") { backStackEntry ->
                val index = backStackEntry.arguments?.getString("orderIndex")?.toIntOrNull() ?: 0
                val selectedOrder = reservationsList[index]

                OrderDetailScreen(
                    innerPadding = innerPadding,
                    navController = navController,
                    order = selectedOrder
                )
            }
            composable("notifications") {
                if (isConsumer) {
                    NotificationScreen(innerPadding = innerPadding)
                } else {
                    ProviderNotificationScreen(innerPadding = innerPadding)
                }
            }

            composable("inventory") {
                InventoryScreen(innerPadding = innerPadding)
            }
            composable("profile") {
                ProfileScreen(innerPadding = innerPadding)
            }
        }
    }
}


