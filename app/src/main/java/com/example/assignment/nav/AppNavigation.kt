package com.example.assignment.nav

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.assignment.data.repository.AuthRepository
import com.example.assignment.data.supabase.AuthService
import com.example.assignment.screen.HomeScreen
import com.example.assignment.screen.ProviderHomeScreen
import com.example.assignment.screen.login.LoginScreen
import com.example.assignment.screen.register.AccountTypeScreen
import com.example.assignment.screen.register.RegisterProviderScreen
import com.example.assignment.screen.register.RegisterSaverScreen
import com.example.assignment.viewmodel.LoginViewModel
import com.example.assignment.viewmodel.RegisterProviderViewModel
import com.example.assignment.viewmodel.RegisterSaverViewModel

@Composable
fun AppNavigation() {
    val navController: NavHostController = rememberNavController()
    val authService = remember { AuthService() }
    val authRepo = remember { AuthRepository(authService) }

    val startDestination = if (authRepo.getCurrentUser() != null) {
        "home_saver"
    } else {
        "login"
    }

    NavHost(navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(
                viewModel = LoginViewModel(authRepo),
                onNavigateToRegister = { navController.navigate("account_type") },
                onLoginSuccess = {
                    navController.navigate("home_saver") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("account_type") {
            AccountTypeScreen(
                onSelectSaver = { navController.navigate("register_saver") },
                onSelectProvider = { navController.navigate("register_provider") }
            )
        }

        // 注册（食品拯救者)
        composable("register_saver") {
            RegisterSaverScreen(
                viewModel = RegisterSaverViewModel(authRepo),
                onRegisterSuccess = { navController.popBackStack("login", false) },
                onBack = { navController.popBackStack() }
            )
        }

        // 注册（食品提供者
        composable("register_provider") {
            RegisterProviderScreen(
                viewModel = RegisterProviderViewModel(authRepo),
                onRegisterSuccess = { navController.popBackStack("login", false) },
                onBack = { navController.popBackStack() }
            )
        }

        // 普通用户主页
        composable("home_saver") {
            val innerPadding = PaddingValues(0.dp)
            HomeScreen(
                innerPadding = innerPadding,
                navController = navController
            )
        }

        // 商家主页
        composable("home_provider") {
            val innerPadding = PaddingValues(0.dp)
            ProviderHomeScreen(
                innerPadding = innerPadding,
                navController = navController
            )
        }

        composable("notifications") {
            Text("Notifications Screen")
        }

        // 食品详情，参数为 foodIndex
        composable("food_detail/{foodIndex}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("foodIndex")?.toIntOrNull() ?: 0
            Text("Food Detail for index $index")
        }

        composable("add") {
            Text("Add Food Listing")
        }

        composable("restaurant_detail") {
            Text("Restaurant Detail")
        }
    }
}