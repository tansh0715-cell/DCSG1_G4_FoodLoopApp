package com.example.assignment.nav

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.assignment.data.UserPreferencesManager
import com.example.assignment.data.repository.AuthRepository
import com.example.assignment.data.repository.FoodRepository
import com.example.assignment.data.repository.OrderRepository
import com.example.assignment.data.repository.RestaurantRepository
import com.example.assignment.data.supabase.supabase
import com.example.assignment.screen.food.AddFoodScreen
import com.example.assignment.screen.AppNavigationBar
import com.example.assignment.screen.food.FoodDetailScreen
import com.example.assignment.screen.home.HomeScreen
import com.example.assignment.screen.notification.NotificationScreen
import com.example.assignment.screen.order.OrderDetailScreen
import com.example.assignment.screen.order.OrderScreen
import com.example.assignment.screen.home.ProviderHomeScreen
import com.example.assignment.screen.notification.ProviderNotificationScreen
import com.example.assignment.screen.order.ProviderOrderScreen
import com.example.assignment.screen.restaurant.RestaurantDetailScreen
import com.example.assignment.screen.inventoryModule.AddItemScreen
import com.example.assignment.screen.inventoryModule.InventoryScreen
import com.example.assignment.screen.login.ForgotPasswordScreen
import com.example.assignment.screen.login.LoginScreen
import com.example.assignment.screen.login.ResetPasswordScreen
import com.example.assignment.screen.payment.PaymentScreen
import com.example.assignment.screen.profileModule.ProfileScreen
import com.example.assignment.screen.register.RegisterScreen
import com.example.assignment.screen.register.RegisterTypeScreen
import com.example.assignment.viewmodel.food.AddFoodViewModel
import com.example.assignment.viewmodel.food.AddFoodViewModelFactory
import com.example.assignment.viewmodel.food.FoodDetailViewModel
import com.example.assignment.viewmodel.food.FoodDetailViewModelFactory
import com.example.assignment.viewmodel.home.HomeViewModel
import com.example.assignment.viewmodel.home.HomeViewModelFactory
import com.example.assignment.viewmodel.order.OrderViewModel
import com.example.assignment.viewmodel.order.OrderViewModelFactory
import com.example.assignment.viewmodel.restaurant.RestaurantDetailViewModel
import com.example.assignment.viewmodel.restaurant.RestaurantDetailViewModelFactory
import io.github.jan.supabase.auth.auth

@Composable
fun AppNavigation(
    navController: NavHostController,
    authRepository: AuthRepository,
    startDestination: String = "LOGIN"

) {

    val AppPadding = PaddingValues(0.dp)
    val foodRepository = remember {
        FoodRepository(supabase)
    }
    val restaurantRepository = remember {
        RestaurantRepository(supabase)
    }
    val orderRepository = remember {
        OrderRepository(supabase)
    }

    val currentUserId = supabase.auth.currentUserOrNull()?.id.orEmpty()
    val context = LocalContext.current
    val userPreferencesManager = remember { UserPreferencesManager(context) }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("LOGIN") {
            LoginScreen(
                onFoodSaverLogin = {
                    navController.navigate("HOME") {
                        popUpTo("LOGIN") { inclusive = true }
                    }
                },
                onFoodProviderLogin = {
                    navController.navigate("PROVIDER_HOME") {
                        popUpTo("LOGIN") { inclusive = true }
                    }
                },
                onRegister = { navController.navigate("REGISTER") },
                onForgotPassword = { navController.navigate("FORGOT_PASSWORD") },
                authRepository = authRepository
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

        composable("HOME") {
            val homeViewModel: HomeViewModel =
                viewModel(
                    factory = HomeViewModelFactory(
                        foodRepository,
                        restaurantRepository,
                        orderRepository,
                        authRepository
                    )
                )

            Scaffold(
                bottomBar = {
                    AppNavigationBar(
                        navController = navController,
                        isConsumer = true
                    )
                }
            ) { innerPadding ->
                HomeScreen(
                    innerPadding = innerPadding,
                    navController = navController,
                    viewModel = homeViewModel
                )
            }
        }

        composable(
            route = "restaurant/{restaurantId}?distanceMeters={distanceMeters}",
            arguments = listOf(
                navArgument("restaurantId") {
                    type = NavType.StringType
                },
                navArgument("distanceMeters") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { entry ->

            val restaurantId =
                entry.arguments
                    ?.getString("restaurantId")
                    .orEmpty()

            val distanceMeters =
                entry.arguments
                    ?.getString("distanceMeters")
                    ?.toDoubleOrNull()

            val restaurantViewModel:
                    RestaurantDetailViewModel =
                viewModel(
                    factory =
                        RestaurantDetailViewModelFactory(
                            restaurantRepository,
                            foodRepository
                        )
                )

            LaunchedEffect(
                restaurantId
            ) {

                restaurantViewModel.load(
                    restaurantId
                )
            }

            RestaurantDetailScreen(
                innerPadding = PaddingValues(),
                navController = navController,
                viewModel = restaurantViewModel,
                distanceMeters = distanceMeters
            )
        }

        composable(
            route = "food_detail/{foodId}",
            arguments = listOf(
                navArgument("foodId") {
                    type = NavType.StringType
                }
            )
        ) { entry ->

            val foodId =
                entry.arguments
                    ?.getString("foodId")
                    .orEmpty()

            val foodViewModel:
                    FoodDetailViewModel =
                viewModel(
                    factory =
                        FoodDetailViewModelFactory(
                            foodRepository,
                            restaurantRepository
                        )
                )

            val foodState by
            foodViewModel.uiState
                .collectAsStateWithLifecycle()

            LaunchedEffect(
                foodId
            ) {

                foodViewModel.load(
                    foodId
                )
            }

            if (
                foodState.isLoading
            ) {

                Box(
                    modifier =
                        Modifier.fillMaxSize(),
                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        "Loading food..."
                    )
                }

            } else if (
                foodState.food != null
            ) {

                val orderViewModel:
                        OrderViewModel =
                    viewModel(
                        factory =
                            OrderViewModelFactory(
                                orderRepository,
                                currentUserId,
                                foodRepository,
                                restaurantRepository
                            )
                    )

                FoodDetailScreen(
                    innerPadding =
                        PaddingValues(),

                    restaurant =
                        foodState.restaurant,

                    food =
                        foodState.food!!,

                    onBackClick = {
                        navController.popBackStack()
                    },

                    onPurchase = {
                            selectedFoodId,
                            quantity ->

                        if (
                            quantity >= 1 &&
                            quantity <= foodState.food!!.quantity
                        ) {

                            navController.navigate(
                                "PAYMENT/$selectedFoodId/$quantity"
                            )
                        }
                    }
                )

            } else {

                Box(
                    modifier =
                        Modifier.fillMaxSize(),
                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        foodState.errorMessage
                            ?: "Food not found."
                    )
                }
            }
        }

        composable(
            route = "ORDER_DETAIL/{orderId}",
            arguments = listOf(
                navArgument("orderId") {
                    type = NavType.StringType
                }
            )
        ) { entry ->

            val orderId =
                entry.arguments
                    ?.getString("orderId")
                    .orEmpty()

            val orderViewModel:
                    OrderViewModel =
                viewModel(
                    factory =
                        OrderViewModelFactory(
                            orderRepository,
                            currentUserId,
                            foodRepository,
                            restaurantRepository
                        )
                )

            val selectedOrder by orderViewModel.selectedOrder
                .collectAsStateWithLifecycle()

            LaunchedEffect(
                orderId
            ) {

                orderViewModel.loadOrderById(
                    orderId
                )
            }

            if (
                selectedOrder != null
            ) {

                OrderDetailScreen(
                    innerPadding =
                        PaddingValues(),

                    navController =
                        navController,

                    order =
                        selectedOrder!!,

                    orderViewModel =
                        orderViewModel
                )

            } else {

                Box(
                    modifier =
                        Modifier.fillMaxSize(),
                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        "Loading reservation details..."
                    )
                }
            }
        }

        composable(
            route = "PAYMENT/{foodId}/{quantity}",
            arguments = listOf(
                navArgument("foodId") {
                    type = NavType.StringType
                },
                navArgument("quantity"){
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val foodId = backStackEntry.arguments?.getString("foodId").orEmpty()
            val quantity = backStackEntry.arguments?.getInt("quantity")?: 1
            // Load real food from Supabase
            val foodViewModel:
                    FoodDetailViewModel =
                viewModel(
                    factory =
                        FoodDetailViewModelFactory(
                            foodRepository,
                            restaurantRepository
                        )
                )
            val foodState by foodViewModel.uiState.collectAsStateWithLifecycle()


            // Order ViewModel
            val orderViewModel:
                    OrderViewModel =
                viewModel(
                    factory =
                        OrderViewModelFactory(
                            orderRepository,
                            currentUserId,
                            foodRepository,
                            restaurantRepository
                        )
                )

            LaunchedEffect(foodId) {
                foodViewModel.load(foodId)
            }

            if(foodState.isLoading){
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ){
                    Text("Loading payment...")
                }
            } else if (foodState.food != null) {

                val food = foodState.food!!
                val total = food.price * quantity

                PaymentScreen(

                    foodName = food.name,
                    quantity = quantity,
                    total = total,
                    onPaymentSuccess = {

                        // Payment succeeded -> create the real order.
                        orderViewModel.createOrder(
                            foodId = food.id,
                            quantity = quantity,
                            paymentSuccess = true,

                            onSuccess = { createdOrder ->
                                navController.navigate(
                                    "ORDER"
                                ) {
                                    popUpTo(
                                        "PAYMENT/$foodId/$quantity"
                                    ) {
                                        inclusive = true
                                    }
                                }
                            },
                            onError = { error ->
                                error.printStackTrace()
                            }
                        )
                    },

                    onBack = {
                        navController.popBackStack()
                    }
                )

            } else {

                Box(
                    modifier =
                        Modifier.fillMaxSize(),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        foodState.errorMessage
                            ?: "Food not found."
                    )
                }
            }
        }

        composable("PROVIDER_HOME") {
            val providerId = supabase.auth.currentUserOrNull()?.id.orEmpty()

            val homeViewModel: HomeViewModel =
                viewModel(
                    factory =
                        HomeViewModelFactory(
                            foodRepository,
                            restaurantRepository,
                            orderRepository,
                            authRepository
                        )
                )

            Scaffold(
                bottomBar = {
                    AppNavigationBar(
                        navController = navController,
                        isConsumer = false
                    )
                }
            ) { innerPadding ->

                ProviderHomeScreen(
                    innerPadding = innerPadding,
                    navController = navController,
                    viewModel = homeViewModel,
                    providerId = providerId
                )
            }
        }

        composable("ORDER") {

            val orderViewModel: OrderViewModel =
                viewModel(
                    factory =
                        OrderViewModelFactory(
                            orderRepository,
                            currentUserId,
                            foodRepository,
                            restaurantRepository
                        )
                )

            Scaffold(
                bottomBar = {
                    AppNavigationBar(
                        navController = navController,
                        isConsumer = true
                    )
                }
            ) { innerPadding ->

                OrderScreen(
                    innerPadding = innerPadding,
                    navController = navController,
                    orderViewModel = orderViewModel
                )
            }
        }

        composable("PROVIDER_ORDER") {

            val orderViewModel: OrderViewModel =
                viewModel(
                    factory =
                        OrderViewModelFactory(
                            orderRepository,
                            currentUserId,
                            foodRepository,
                            restaurantRepository
                        )
                )

            Scaffold(
                bottomBar = {
                    AppNavigationBar(
                        navController = navController,
                        isConsumer = false
                    )
                }
            ) { innerPadding ->

                ProviderOrderScreen(
                    innerPadding = innerPadding,
                    navController = navController,
                    orderViewModel = orderViewModel
                )
            }
        }

        composable("ADD_FOOD") {
            val providerId = supabase.auth.currentUserOrNull()?.id.orEmpty()

            val addFoodViewModel: AddFoodViewModel =
                viewModel(
                    factory =
                        AddFoodViewModelFactory(
                            repository = foodRepository,
                            restaurantRepository = restaurantRepository,
                            providerId = providerId
                        )
                )
            Scaffold(
                bottomBar = {
                    AppNavigationBar(
                        navController = navController,
                        isConsumer = false
                    )
                }
            ) { innerPadding ->
                AddFoodScreen(
                    navController = navController,
                    innerPadding = innerPadding,
                    viewModel = addFoodViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
        composable("ADD_FOOD/{foodId}") { backStackEntry ->

            val providerId =
                supabase.auth.currentUserOrNull()?.id.orEmpty()

            val foodId =
                backStackEntry.arguments?.getString("foodId")

            val addFoodViewModel: AddFoodViewModel =
                viewModel(
                    factory =
                        AddFoodViewModelFactory(
                            repository = foodRepository,
                            restaurantRepository = restaurantRepository,
                            providerId = providerId
                        )
                )

            LaunchedEffect(foodId) {

                if (!foodId.isNullOrBlank()) {
                    addFoodViewModel.loadFoodForEdit(foodId)
                }
            }

            Scaffold(
                bottomBar = {
                    AppNavigationBar(
                        navController = navController,
                        isConsumer = false
                    )
                }
            ) { innerPadding ->

                AddFoodScreen(
                    navController = navController,
                    innerPadding = innerPadding,
                    viewModel = addFoodViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }

        composable("NOTIFICATIONS") {

            val notificationViewModel: OrderViewModel =
                viewModel(
                    factory = OrderViewModelFactory(
                        orderRepository,
                        currentUserId,
                        foodRepository,
                        restaurantRepository
                    )
                )

            Scaffold(
                bottomBar = {
                    AppNavigationBar(
                        navController = navController,
                        isConsumer = true
                    )
                }
            ) { innerPadding ->

                NotificationScreen(
                    innerPadding = innerPadding,
                    orderViewModel = notificationViewModel
                )
            }
        }
        composable("PROVIDER_NOTIFICATIONS") {
            val notificationViewModel: OrderViewModel =
                viewModel(
                    factory = OrderViewModelFactory(
                        orderRepository,
                        currentUserId,
                        foodRepository,
                        restaurantRepository
                    )
                )

            ProviderNotificationScreen(
                innerPadding = PaddingValues(),
                orderViewModel = notificationViewModel
            )
        }

        composable("INVENTORY_SCREEN"){
            InventoryScreen(
                innerPadding = AppPadding,
                onAdd = {
                    navController.navigate("ADD_INVENTORY")
                }
            )
        }

        composable("ADD_INVENTORY"){
            AddItemScreen(onBack = {navController.popBackStack()})
        }

        composable("PROFILE_PROVIDER") {

            Scaffold(
                bottomBar = {
                    AppNavigationBar(
                        navController = navController,
                        isConsumer = false
                    )
                }
            ) { innerPadding ->

                ProfileScreen(
                    innerPadding = innerPadding,
                    navController = navController,
                    authRepository = authRepository,
                    userPreferencesManager = userPreferencesManager
                )
            }
        }

        composable("PROFILE_CONSUMER") {

            Scaffold(
                bottomBar = {
                    AppNavigationBar(
                        navController = navController,
                        isConsumer = true
                    )
                }
            ) { innerPadding ->

                ProfileScreen(
                    innerPadding = innerPadding,
                    navController = navController,
                    authRepository = authRepository,
                    userPreferencesManager = userPreferencesManager
                )
            }
        }
    }
}