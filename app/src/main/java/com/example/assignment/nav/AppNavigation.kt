package com.example.assignment.nav

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.assignment.data.repository.AuthRepository
import com.example.assignment.data.repository.FoodRepository
import com.example.assignment.data.supabase.AuthService
import com.example.assignment.data.supabase.supabase
import com.example.assignment.model.AccountType
import com.example.assignment.model.FoodListing
import com.example.assignment.screen.AddFoodScreen
import com.example.assignment.screen.FoodDetailScreen
import com.example.assignment.screen.HomeScreen
import com.example.assignment.screen.ProviderHomeScreen
import com.example.assignment.screen.RestaurantDetailScreen
import com.example.assignment.screen.InventoryScreen
import com.example.assignment.screen.OrderScreen
import com.example.assignment.screen.ProviderOrderScreen
import com.example.assignment.screen.ProfileScreen
import com.example.assignment.screen.login.LoginScreen
import com.example.assignment.screen.register.AccountTypeScreen
import com.example.assignment.screen.register.RegisterProviderScreen
import com.example.assignment.screen.register.RegisterSaverScreen
import com.example.assignment.viewmodel.AddFoodViewModelFactory
import com.example.assignment.viewmodel.HomeViewModel
import com.example.assignment.viewmodel.HomeViewModelFactory
import com.example.assignment.viewmodel.LoginViewModel
import com.example.assignment.viewmodel.RegisterProviderViewModel
import com.example.assignment.viewmodel.RegisterSaverViewModel

@Composable
fun AppNavigation() {
    val navController: NavHostController = rememberNavController()
    val authService = remember { AuthService() }
    val authRepo = remember { AuthRepository(authService) }
    val foodRepository = remember { FoodRepository(supabase) }

    val currentUser = authRepo.getCurrentUser()
    val isProvider = currentUser?.userMetadata
        ?.get("account_type")
        ?.toString()
        ?.trim('"') == AccountType.FOOD_PROVIDER.name

    val startDestination = when {
        currentUser == null -> "login"
        isProvider -> "home_provider"
        else -> "home_saver"
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(
                viewModel = LoginViewModel(authRepo),
                onNavigateToRegister = { navController.navigate("account_type") },
                onLoginSuccess = {
                    // LoginScreen is unchanged. Role routing happens here using Supabase user metadata.
                    val provider = authRepo.getCurrentUser()
                        ?.userMetadata
                        ?.get("account_type")
                        ?.toString()
                        ?.trim('"') == AccountType.FOOD_PROVIDER.name

                    val destination = if (provider) "home_provider" else "home_saver"

                    navController.navigate(destination) {
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

        composable("register_saver") {
            RegisterSaverScreen(
                viewModel = RegisterSaverViewModel(authRepo),
                onRegisterSuccess = { navController.popBackStack("login", false) },
                onBack = { navController.popBackStack() }
            )
        }

        composable("register_provider") {
            RegisterProviderScreen(
                viewModel = RegisterProviderViewModel(authRepo),
                onRegisterSuccess = { navController.popBackStack("login", false) },
                onBack = { navController.popBackStack() }
            )
        }

        // SAVER HOME: reads all real food listings from Supabase.
        composable("home_saver") {
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModelFactory(foodRepository)
            )
            HomeScreen(
                navController = navController,
                viewModel = homeViewModel
            )
        }

        // PROVIDER HOME: reads only this provider's listings from Supabase.
        composable("home_provider") {
            val providerId = authRepo.getCurrentUser()?.id.orEmpty()
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModelFactory(foodRepository)
            )

            ProviderHomeScreen(
                innerPadding = PaddingValues(0.dp),
                navController = navController,
                viewModel = homeViewModel,
                providerId = providerId
            )
        }

        composable("notifications") {
            Text("Notifications Screen")
        }

        composable("order") {
            if (isProvider) {
                ProviderOrderScreen(
                    innerPadding = PaddingValues(0.dp),
                    navController = navController
                )
            } else {
                OrderScreen(
                    innerPadding = PaddingValues(0.dp),
                    navController = navController
                )
            }
        }

        composable("inventory") {
            InventoryScreen(PaddingValues(0.dp))
        }

        composable("profile") {
            ProfileScreen(PaddingValues(0.dp))
        }

        // Food Detail now uses the real food UUID, not a dummy list index.
        composable("food_detail/{foodId}") { backStackEntry ->
            val foodId = backStackEntry.arguments?.getString("foodId")
            var food by remember(foodId) { mutableStateOf<FoodListing?>(null) }
            var error by remember(foodId) { mutableStateOf<String?>(null) }

            LaunchedEffect(foodId) {
                if (foodId.isNullOrBlank()) {
                    error = "Food listing not found"
                    return@LaunchedEffect
                }

                try {
                    food = foodRepository.getFoodListingById(foodId)
                } catch (e: Exception) {
                    error = e.message ?: "Failed to load food"
                }
            }

            when {
                food != null -> FoodDetailScreen(
                    innerPadding = PaddingValues(0.dp),
                    food = food!!,
                    onBackClick = { navController.popBackStack() }
                )
                error != null -> Text(error!!)
                else -> Text("Loading food...")
            }
        }

        // ADD FOOD: create a new listing.
        composable("add") {
            val providerId = authRepo.getCurrentUser()?.id.orEmpty()
            val addFoodViewModel = viewModel<com.example.assignment.viewmodel.AddFoodViewModel>(
                factory = AddFoodViewModelFactory(foodRepository, providerId)
            )

            AddFoodScreen(
                navController = navController,
                innerPadding = PaddingValues(0.dp),
                viewModel = addFoodViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // EDIT FOOD: same FoodListing ID is loaded and preserved during upsert.
        composable("add/{foodId}") { backStackEntry ->
            val providerId = authRepo.getCurrentUser()?.id.orEmpty()
            val foodId = backStackEntry.arguments?.getString("foodId")
            val addFoodViewModel = viewModel<com.example.assignment.viewmodel.AddFoodViewModel>(
                factory = AddFoodViewModelFactory(foodRepository, providerId)
            )

            LaunchedEffect(foodId) {
                if (!foodId.isNullOrBlank()) {
                    addFoodViewModel.loadFoodForEdit(foodId)
                }
            }

            AddFoodScreen(
                navController = navController,
                innerPadding = PaddingValues(0.dp),
                viewModel = addFoodViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("restaurant_detail") {
            RestaurantDetailScreen(
                innerPadding = PaddingValues(0.dp),
                navController = navController,
                foods = emptyList()
            )
        }
    }
}