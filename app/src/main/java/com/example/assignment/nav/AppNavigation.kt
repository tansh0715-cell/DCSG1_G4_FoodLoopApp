package com.example.assignment.nav

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.assignment.data.repository.AuthRepository
import com.example.assignment.screen.AppNavigationBar
import com.example.assignment.screen.home.FoodProviderHome
import com.example.assignment.screen.home.FoodSaverHome
import com.example.assignment.screen.inventoryModule.AddItemScreen
import com.example.assignment.screen.inventoryModule.InventoryScreen
import com.example.assignment.screen.login.ForgotPasswordScreen
import com.example.assignment.screen.login.LoginScreen
import com.example.assignment.screen.login.ResetPasswordScreen
import com.example.assignment.screen.register.RegisterScreen
import com.example.assignment.screen.register.RegisterTypeScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    authRepository: AuthRepository
) {

    var AppPadding = PaddingValues(0.dp)
    NavHost(
        navController = navController,
        startDestination = "LOGIN"
    ) {
        composable("LOGIN") {
            LoginScreen(
                onFoodSaverLogin = {
                    navController.navigate("FOOD_SAVE_HOME") {
                        popUpTo("LOGIN") { inclusive = true }
                    }
                },
                onFoodProviderLogin = {
                    navController.navigate("FOOD_PROVIDER_HOME") {
                        popUpTo("LOGIN") { inclusive = true }
                    }
                },
                onRegister = { navController.navigate("REGISTER") },
                onForgotPassword = { navController.navigate("FORGOT_PASSWORD") }
            )
        }

        composable("REGISTER") {
            RegisterTypeScreen(
                onSelectedFoodSaver = {
                    navController.navigate("REGISTER_FORM/FOOD_SAVER")
                },
                onSelectedFoodProvider = {
                    navController.navigate("REGISTER_FORM/FOOD_PROVIDER")
                },
                onBackToLogin = { navController.popBackStack() }
            )
        }

        composable(
            route = "REGISTER_FORM/{accountType}",
            arguments = listOf(navArgument("accountType") { type = NavType.StringType })
        ) { backStackEntry ->
            val accountType = backStackEntry.arguments?.getString("accountType") ?: "FOOD_SAVER"

            RegisterScreen(
                accountType = accountType,
                authRepository = authRepository,
                onRegisterSuccess = {
                    navController.navigate("LOGIN") {
                        popUpTo("LOGIN") { inclusive = true }
                    }
                },
                onBackToChoose = { navController.popBackStack() }
            )
        }

        composable("FORGOT_PASSWORD") {
            ForgotPasswordScreen(
                authRepository = authRepository,
                onBackToLogin = { navController.popBackStack() }
            )
        }

        composable("RESET_PASSWORD") {
            ResetPasswordScreen(
                authRepository = authRepository,
                onPasswordUpdated = {
                    navController.navigate("LOGIN") {
                        popUpTo("LOGIN") { inclusive = true }
                    }
                },
                onBackToLogin = { navController.popBackStack() }
            )
        }

        composable("FOOD_SAVE_HOME") {
            Scaffold(bottomBar = {AppNavigationBar(navController = navController, isConsumer = true)}) { innerPadding ->
                AppPadding = innerPadding
                FoodSaverHome(innerPadding)
            }
        }

        composable("FOOD_PROVIDER_HOME") {
            FoodProviderHome()
        }

        composable("INVENTORY_SCREEN"){
            InventoryScreen(innerPadding = AppPadding, onAdd = {
                navController.navigate("ADD_INVENTORY") })
        }

        composable("ADD_INVENTORY"){
            AddItemScreen(onBack = {navController.popBackStack()})
        }

    }
}