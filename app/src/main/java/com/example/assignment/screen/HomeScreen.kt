package com.example.assignment.screen

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.assignment.R
import com.example.assignment.components.FoodCard
import com.example.assignment.components.ProviderFoodCard
import com.example.assignment.components.StatCard
import com.example.assignment.location.LocationTracker
import com.example.assignment.ui.theme.BackgroundColor
import com.example.assignment.ui.theme.PrimaryGreen
import com.example.assignment.ui.theme.SafeColor
import com.example.assignment.ui.theme.SecondaryGreen
import com.example.assignment.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    innerPadding: PaddingValues,
    navController: NavController,
    viewModel: HomeViewModel
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val locationTracker = remember {
        LocationTracker(
            context.applicationContext
        )
    }

    val locationPermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            val granted =
                permissions[
                    Manifest.permission.ACCESS_FINE_LOCATION
                ] == true ||
                        permissions[
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ] == true

            if (granted) {

                locationTracker.start { location ->

                    viewModel.updateUserLocation(
                        latitude = location.latitude,
                        longitude = location.longitude
                    )
                }
            }
        }

    DisposableEffect(Unit) {

        val hasPermission =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {

            locationTracker.start { location ->

                viewModel.updateUserLocation(
                    latitude = location.latitude,
                    longitude = location.longitude
                )
            }

        } else {

            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }

        onDispose {
            locationTracker.stop()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadAllFoods()
        viewModel.loadConsumerNotificationState()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            )
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        //Header (FoodLoop logo & notification)
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Column{
                    Text(text = "FoodLoop", color = MaterialTheme.colorScheme.primary, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                    Text(text = "Reduce waste, save food \uD83C\uDF3F", fontSize = 14.sp, color = MaterialTheme.colorScheme.secondary)
                }

                Box{
                    IconButton(
                        onClick = {navController.navigate("notifications")},
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.White,CircleShape)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.notifications_24dp_e3e3e3_fill0_wght400_grad0_opsz24),
                            contentDescription = "Notification",
                            tint = PrimaryGreen
                        )
                    }

                    if(uiState.hasNotifications){
                        Box( //Red dot
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color.Red)
                                .align(Alignment.TopEnd)
                                .offset(x = 2.dp, y = (-2).dp)
                        )
                    }
                }
            }
        }
        // Nearby Restaurants
        item {

            Text(
                text = "Nearby Restaurants",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            when {

                uiState.locationError != null -> {

                    Text(
                        text = uiState.locationError!!,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                uiState.userLatitude == null ||
                        uiState.userLongitude == null -> {

                    Text(
                        text =
                            "Allow location access to find restaurants within 10 km.",
                        fontSize = 13.sp,
                        color =
                            MaterialTheme.colorScheme.onSecondary
                    )
                }

                uiState.nearbyRestaurants.isEmpty() -> {

                    Text(
                        text =
                            "No restaurants found within 10 km.",
                        fontSize = 13.sp,
                        color =
                            MaterialTheme.colorScheme.onSecondary
                    )
                }

                else -> {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(
                            items = uiState.nearbyRestaurants,
                            key = { it.id }
                        ) { restaurant ->

                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White
                                ),
                                elevation =
                                    CardDefaults.cardElevation(2.dp),

                                modifier = Modifier
                                    .size(
                                        width = 140.dp,
                                        height = 165.dp
                                    )
                                    .clickable {

                                        navController.navigate(
                                            "restaurant/${restaurant.id}?distanceMeters=\${restaurant.distanceMeters}"
                                        )
                                    }
                                    .shadow(
                                        elevation = 8.dp,
                                        shape = RoundedCornerShape(12.dp),
                                        spotColor = Color.Gray,
                                        ambientColor = Color.LightGray
                                    )
                            ) {

                                Column {
                                    AsyncImage(
                                        model = restaurant.imageUrl,
                                        contentDescription = restaurant.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(95.dp)
                                    )

                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalAlignment = Alignment.Start
                                    ) {

                                        Text(
                                            text = restaurant.name,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            color = MaterialTheme.colorScheme.onPrimary
                                        )

                                        Text(
                                            text = String.format(
                                                "%.1f km away",
                                                restaurant.distanceMeters / 1000.0
                                            ),
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSecondary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Categories
        item {

            Text(
                text = "Suggested Food",
                style =
                    MaterialTheme.typography.titleLarge,
                color =
                    MaterialTheme.colorScheme.onPrimary
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            LazyRow(
                horizontalArrangement =
                    Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                items(
                    uiState.categories.size
                ) { index ->

                    val isSelected =
                        uiState.selectedCategoryIndex ==
                                index

                    Surface(
                        shape =
                            RoundedCornerShape(20.dp),
                        color =
                            if (isSelected)
                                PrimaryGreen
                            else
                                SafeColor,
                        modifier =
                            Modifier.height(36.dp),
                        onClick = {
                            viewModel.onCategorySelected(
                                index
                            )
                        }
                    ) {

                        Box(
                            contentAlignment =
                                Alignment.Center,
                            modifier =
                                Modifier.padding(
                                    horizontal = 16.dp
                                )
                        ) {

                            Text(
                                text =
                                    uiState.categories[index],
                                color =
                                    if (isSelected)
                                        BackgroundColor
                                    else
                                        SecondaryGreen,
                                fontSize = 14.sp,
                                fontWeight =
                                    if (isSelected)
                                        FontWeight.Bold
                                    else
                                        FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
        // Customer Food List
        if (uiState.isLoading) {

            item {

                Text(
                    text =
                        "Loading food listings...",
                    color =
                        MaterialTheme.colorScheme.onSecondary
                )
            }

        } else if (uiState.errorMessage != null) {

            item {

                Text(
                    text =
                        uiState.errorMessage!!,
                    color =
                        MaterialTheme.colorScheme.error
                )
            }

        } else if (uiState.foods.isEmpty()) {

            item {

                Text(
                    text =
                        "No surplus food available yet.",
                    color =
                        MaterialTheme.colorScheme.onSecondary
                )
            }

        } else {

            items(
                items = uiState.foods,
                key = { it.id }
            ) { food ->

                FoodCard(
                    food = food,
                    isProvider = false,
                    onEditClick = {},
                    onCardClick = {

                        navController.navigate(
                            "food_detail/${food.id}"
                        )
                    }
                )
            }
        }

        item {

            Spacer(
                modifier = Modifier.height(20.dp)
            )
        }
    }
}

// PROVIDER HOME SCREEN
@Composable
fun ProviderHomeScreen(
    innerPadding: PaddingValues,
    navController: NavController,
    viewModel: HomeViewModel,
    providerId: String
) {

    val uiState by
    viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(providerId) {

        if (providerId.isNotBlank()) {

            viewModel.loadProviderHome(
                providerId
            )
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            )
            .padding(innerPadding)
            .padding(horizontal = 16.dp),
        verticalArrangement =
            Arrangement.spacedBy(20.dp)
    ) {

        // 1. Restaurant Header
        item {

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Surface(
                    shape =
                        RoundedCornerShape(12.dp),
                    color =
                        MaterialTheme.colorScheme
                            .primaryContainer,
                    modifier =
                        Modifier.size(50.dp)
                ) {

                    Box(
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Icon(
                            painter = painterResource(
                                R.drawable.home_4_svgrepo_com
                            ),
                            contentDescription =
                                "Shop",
                            tint =
                                MaterialTheme.colorScheme
                                    .primary,
                            modifier =
                                Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        text =
                            "Restaurant dashboard",
                        fontSize = 12.sp,
                        color =
                            MaterialTheme.colorScheme
                                .onSecondary
                    )

                    Text(
                        text =
                            uiState.restaurantName
                                ?: "Restaurant",
                        fontSize = 22.sp,
                        fontWeight =
                            FontWeight.ExtraBold,
                        color =
                            MaterialTheme.colorScheme
                                .onPrimary,
                        maxLines = 1,
                        overflow =
                            TextOverflow.Ellipsis
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    border =
                        BorderStroke(
                            1.dp,
                            Color(0xFFE2E8F0)
                        ),
                    modifier =
                        Modifier.size(40.dp)
                ) {

                    IconButton(
                        onClick = {
                            navController.navigate(
                                "PROVIDER_NOTIFICATIONS"
                            )
                        }
                    ) {

                        Icon(
                            painter = painterResource(
                                R.drawable.notifications_24dp_e3e3e3_fill0_wght400_grad0_opsz24
                            ),
                            contentDescription =
                                "Notification",
                            tint =
                                MaterialTheme.colorScheme
                                    .primary
                        )
                    }
                }
            }
        }

        // 2. Banner
        item {

            Surface(
                color =
                    MaterialTheme.colorScheme.primary,
                shape =
                    RoundedCornerShape(16.dp),
                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier =
                        Modifier.padding(20.dp)
                ) {

                    Text(
                        text =
                            "TODAY AT A GLANCE",
                        fontSize = 11.sp,
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            Color.White.copy(
                                alpha = 0.8f
                            ),
                        letterSpacing = 1.sp
                    )

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )

                    Text(
                        text =
                            "Small surplus, big impact.",
                        fontSize = 20.sp,
                        fontWeight =
                            FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Spacer(
                        modifier =
                            Modifier.height(4.dp)
                    )

                    Text(
                        text =
                            "Keep good food in the loop with every listing.",
                        fontSize = 13.sp,
                        color =
                            Color.White.copy(
                                alpha = 0.9f
                            )
                    )
                }
            }
        }

        // 3. Dashboard Statistics
        item {

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                StatCard(
                    modifier =
                        Modifier.weight(1f),
                    title = "Food Listed",
                    value =
                        uiState.totalFoodCount
                            .toString(),
                    iconRes =
                        R.drawable.fridge_svgrepo_com
                )

                StatCard(
                    modifier =
                        Modifier.weight(1f),
                    title = "Active Food",
                    value =
                        uiState.activeFoodCount
                            .toString(),
                    iconRes =
                        R.drawable.add_circle_24dp_e3e3e3_fill0_wght400_grad0_opsz24
                )

                StatCard(
                    modifier =
                        Modifier.weight(1f),
                    title = "Reservations",
                    value =
                        uiState.reservationCount
                            .toString(),
                    iconRes =
                        R.drawable.order_svgrepo_com
                )
            }
        }

        // 4. Manage Listings Header
        item {

            Column {

                Text(
                    text =
                        "Manage your listings",
                    fontSize = 12.sp,
                    color =
                        MaterialTheme.colorScheme
                            .onSecondary
                )

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceBetween,
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Text(
                        text =
                            "Your Active Surplus Food",
                        style =
                            MaterialTheme.typography
                                .titleLarge,
                        color =
                            MaterialTheme.colorScheme
                                .onPrimary
                    )

                    Text(
                        text = "+ Add",
                        fontSize = 14.sp,
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            MaterialTheme.colorScheme
                                .primary,
                        modifier =
                            Modifier
                                .clickable {
                                    navController.navigate(
                                        "ADD_FOOD"
                                    )
                                }
                                .padding(4.dp)
                    )
                }
            }
        }

        // 5. Provider Food Listings
        if (providerId.isBlank()) {

            item {

                Text(
                    text =
                        "Please log in as a provider first.",
                    color =
                        MaterialTheme.colorScheme.error
                )
            }

        } else if (uiState.isLoading) {

            item {

                Text(
                    text =
                        "Loading your food listings..."
                )
            }

        } else if (uiState.errorMessage != null) {

            item {

                Text(
                    text =
                        uiState.errorMessage!!,
                    color =
                        MaterialTheme.colorScheme.error
                )
            }

        } else if (uiState.foods.isEmpty()) {

            item {

                Text(
                    text =
                        "You have not posted any surplus food yet.",
                    color =
                        MaterialTheme.colorScheme
                            .onSecondary
                )
            }

        } else {

            items(
                items = uiState.foods,
                key = { it.id }
            ) { food ->

                ProviderFoodCard(
                    food = food,

                    onEdit = {

                        navController.navigate(
                            "ADD_FOOD/${food.id}"
                        )
                    },

                    onDelete = {

                        viewModel.deleteFood(
                            foodId = food.id,
                            providerId = providerId
                        )
                    }
                )
            }
        }

        item {

            Spacer(
                modifier = Modifier.height(20.dp)
            )
        }
    }
}