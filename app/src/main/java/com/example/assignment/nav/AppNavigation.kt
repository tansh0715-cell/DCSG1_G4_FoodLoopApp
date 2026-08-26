package com.example.assignment.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.assignment.data.repository.AuthRepository
import com.example.assignment.screen.home.FoodProviderHome
import com.example.assignment.screen.home.FoodSaverHome
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
            FoodSaverHome()
        }

        composable("FOOD_PROVIDER_HOME") {
            FoodProviderHome()
        }
    }
}