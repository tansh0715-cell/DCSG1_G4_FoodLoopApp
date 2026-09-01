package com.example.assignment.screen.profileModule

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.assignment.R
import com.example.assignment.data.UserPreferencesManager
import com.example.assignment.data.repository.AuthRepository
import com.example.assignment.data.repository.ProfileRepository
import com.example.assignment.data.supabase.supabase
import com.example.assignment.viewmodel.order.OrderViewModel
import com.example.assignment.viewmodel.profile.ProfileViewModel
import com.example.assignment.viewmodel.profile.ProfileViewModelFactory
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    innerPadding: PaddingValues,
    navController: NavController,
    authRepository: AuthRepository,
    userPreferencesManager: UserPreferencesManager,
    orderViewModel: OrderViewModel? = null
) {

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Profile ViewModel - loads real user data from Supabase
    val currentUserId = supabase.auth.currentUserOrNull()?.id.orEmpty()
    val profileRepository = remember { ProfileRepository(supabase) }
    val profileViewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(profileRepository, currentUserId)
    )

    // Reload profile when screen resumes (e.g., after editing)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                profileViewModel.loadProfile()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // ==============================
    // Get orders from OrderViewModel
    // ==============================

    val orders by orderViewModel
        ?.orders
        ?.collectAsStateWithLifecycle()
        ?: androidx.compose.runtime.remember {
            androidx.compose.runtime.mutableStateOf(emptyList())
        }

    // Food information for each order
    val foodsByOrderId by orderViewModel
        ?.foodByOrderId
        ?.collectAsStateWithLifecycle()
        ?: androidx.compose.runtime.remember {
            androidx.compose.runtime.mutableStateOf(emptyMap())
        }


    // ==============================
    // Calculate Saver statistics
    // ==============================

    val mealsSaved = orders.sumOf { order ->
        order.quantity
    }

    val reservationCount = orders.size

    val moneySaved = orders.sumOf { order ->

        val food = foodsByOrderId[order.id]

        if (food != null) {

            val savingPerFood =
                (food.originalPrice - food.price)
                    .coerceAtLeast(0.0)

            savingPerFood * order.quantity

        } else {
            0.0
        }
    }


    // ==============================
    // Load orders when screen opens
    // ==============================

    LaunchedEffect(orderViewModel) {

        orderViewModel?.loadConsumerOrders()

    }


    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {

        // ==============================
        // Profile header
        // ==============================

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {

            Card(
                colors = CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme.colorScheme.background
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
            ) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {

                    Image(
                        painter = painterResource(
                            R.drawable.ic_launcher_background
                        ),
                        contentDescription =
                            "ProfilePicture",
                        contentScale =
                            ContentScale.Crop,
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(80.dp)
                    )

                    if (profileViewModel.isLoading && !profileViewModel.isProfileLoaded) {
                        Box(
                            modifier = Modifier.padding(5.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    } else {
                        // Centered name only - gray email/phone removed per request
                        Text(
                            text = when {
                                profileViewModel.role == "FOOD_PROVIDER" && profileViewModel.restaurantName.isNotBlank() -> profileViewModel.restaurantName
                                profileViewModel.role == "FOOD_SAVER" && profileViewModel.name.isNotBlank() -> profileViewModel.name
                                profileViewModel.isLoading -> "Loading..."
                                else -> "User"
                            },
                            style =
                                MaterialTheme.typography.titleLarge,
                            modifier =
                                Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ==============================
        // Statistics - only for Food Saver (needs orderViewModel)
        // ==============================

        if (orderViewModel != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                horizontalArrangement =
                    Arrangement.SpaceEvenly
            ) {

            // Meals Saved
            ElevatedCard(
                modifier = Modifier.size(
                    width = 100.dp,
                    height = 80.dp
                )
            ) {

                Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 15.dp)
                ) {

                    Text(
                        text = mealsSaved.toString(),
                        color =
                            MaterialTheme.colorScheme.primary,
                        style =
                            MaterialTheme.typography.headlineMedium
                    )

                    Text(
                        text = "Meals Saved",
                        color =
                            MaterialTheme.colorScheme.onSecondary,
                        style =
                            MaterialTheme.typography.labelMedium
                    )
                }
            }


            // Money Saved
            ElevatedCard(
                modifier = Modifier.size(
                    width = 120.dp,
                    height = 80.dp
                )
            ) {

                Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 15.dp)
                ) {

                    Text(
                        text = "RM %.2f".format(moneySaved),
                        color =
                            MaterialTheme.colorScheme.primary,
                        style =
                            MaterialTheme.typography.headlineMedium
                    )

                    Text(
                        text = "Money Saved",
                        color =
                            MaterialTheme.colorScheme.onSecondary,
                        style =
                            MaterialTheme.typography.labelMedium
                    )
                }
            }


            // Reservations
            ElevatedCard(
                modifier = Modifier.size(
                    width = 100.dp,
                    height = 80.dp
                )
            ) {

                Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 15.dp)
                ) {

                    Text(
                        text = reservationCount.toString(),
                        color =
                            MaterialTheme.colorScheme.primary,
                        style =
                            MaterialTheme.typography.headlineMedium
                    )

                    Text(
                        text = "Reservation",
                        color =
                            MaterialTheme.colorScheme.onSecondary,
                        style =
                            MaterialTheme.typography.labelMedium
                    )
                }
            }
            }
        }


        // ==============================
        // Profile options
        // ==============================

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {

                ListItem(
                    headlineContent = {
                        Text(text = "Edit profile")
                    },
                    trailingContent = {

                        Icon(
                            painter = painterResource(
                                R.drawable.arrow_forward_24dp_cccccc_fill0_wght400_grad0_opsz24
                            ),
                            contentDescription =
                                "Forward"
                        )
                    },
                    colors = ListItemDefaults.colors(
                        containerColor =
                            MaterialTheme.colorScheme.background
                    ),
                    modifier = Modifier.clickable {
                        navController.navigate("EDIT_PROFILE")
                    }
                )


                ListItem(
                    headlineContent = {
                        Text(text = "Achievement")
                    },
                    trailingContent = {

                        Icon(
                            painter = painterResource(
                                R.drawable.arrow_forward_24dp_cccccc_fill0_wght400_grad0_opsz24
                            ),
                            contentDescription =
                                "Forward"
                        )
                    },
                    colors = ListItemDefaults.colors(
                        containerColor =
                            MaterialTheme.colorScheme.background
                    ),
                    modifier = Modifier.clickable {

                        navController.navigate("achievement") {
                            launchSingleTop = true
                        }

                    }
                )


                ListItem(
                    headlineContent = {
                        Text(
                            text = "Reservation history"
                        )
                    },
                    trailingContent = {

                        Icon(
                            painter = painterResource(
                                R.drawable.arrow_forward_24dp_cccccc_fill0_wght400_grad0_opsz24
                            ),
                            contentDescription =
                                "Forward"
                        )
                    },
                    colors = ListItemDefaults.colors(
                        containerColor =
                            MaterialTheme.colorScheme.background
                    ),
                    modifier = Modifier.clickable {
                        navController.navigate("RESERVATION_HISTORY")
                    }
                )


                ListItem(
                    headlineContent = {
                        Text(
                            text = "Change password"
                        )
                    },
                    trailingContent = {

                        Icon(
                            painter = painterResource(
                                R.drawable.arrow_forward_24dp_cccccc_fill0_wght400_grad0_opsz24
                            ),
                            contentDescription =
                                "Forward"
                        )
                    },
                    colors = ListItemDefaults.colors(
                        containerColor =
                            MaterialTheme.colorScheme.background
                    ),
                    modifier = Modifier.clickable {

                    }
                )


                // ==============================
                // Logout
                // ==============================

                ListItem(
                    headlineContent = {

                        Text(
                            text = "Logout",
                            color =
                                MaterialTheme.colorScheme.error
                        )
                    },

                    leadingContent = {

                        Icon(
                            painter = painterResource(
                                R.drawable.ic_launcher_foreground
                            ),
                            contentDescription =
                                "Logout",
                            tint =
                                MaterialTheme.colorScheme.error
                        )
                    },

                    colors = ListItemDefaults.colors(
                        containerColor =
                            MaterialTheme.colorScheme.background
                    ),

                    modifier = Modifier.clickable {

                        scope.launch {

                            try {

                                userPreferencesManager.clear()

                                authRepository.logout()

                                navController.navigate("LOGIN") {

                                    popUpTo(0) {
                                        inclusive = true
                                    }
                                }

                            } catch (e: Exception) {

                                e.printStackTrace()

                            }
                        }
                    }
                )
            }
        }
    }
}
