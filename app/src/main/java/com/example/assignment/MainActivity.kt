package com.example.assignment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.assignment.ui.theme.AssignmentTheme
import com.example.assignment.ui.theme.BackgroundColor
import com.example.assignment.ui.theme.BorderYellow
import com.example.assignment.ui.theme.PrimaryBlue
import com.example.assignment.ui.theme.PrimaryGreen
import com.example.assignment.ui.theme.PrimaryTextColor
import com.example.assignment.ui.theme.PrimaryYellow
import com.example.assignment.ui.theme.SafeColor
import com.example.assignment.ui.theme.SecondaryBlue
import com.example.assignment.ui.theme.SecondaryGreen
import com.example.assignment.ui.theme.SecondaryTextColor
import com.example.assignment.ui.theme.SecondaryYellow
import com.example.assignment.ui.theme.navigationItemColors
import com.example.assignment.ui.theme.textErrorColor
import kotlin.collections.listOf as listOf

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

//Dummy Data
//food list
val foodList = listOf(
    HomeFoodItem(R.drawable.cinnamon_roll,"Cinnamon Roll", "Soft cinnamon roll with sweet cinnamon filling and icing drizzle.",15.00,3,"6:30-7:30 PM",  70),
    HomeFoodItem(R.drawable.nasi_lemak,"Nasi Goreng Special", "Flavorful chicken fried rice with vegetables and egg.",18.00, 5, "7-9 PM",  45),
    HomeFoodItem(R.drawable.sushi,"Sushi Surprise Bag", "",12.00, 2, "5-7 PM",  25)
)
val restaurantSpecificFoods = listOf(
    HomeFoodItem(
        imageResId = R.drawable.cinnamon_roll,
        title = "Cinnamon Roll",
        description = "Soft cinnamon roll with sweet cinnamon filling and icing drizzle.",
        oriPrice = 15.00,
        quantity = 3,
        timeLabel = "6:30 - 7:30 PM",
        discountPercentage = 70
    ),
    HomeFoodItem(
        imageResId = R.drawable.croissant,
        title = "Croissant",
        description = "Flaky and buttery croissant, freshly baked and available at a discounted price.",
        oriPrice = 8.00,
        quantity = 2,
        timeLabel = "6:30 - 7:30 PM",
        discountPercentage = 50
    )
)
val reservationsList = listOf(
    Reservation(
        orderId = "RSV-20260718-001",
        imageResId = R.drawable.croissant,
        foodName = "Croissant Set",
        restaurantName = "Boulangerie Bakery",
        pickupTimeRange = "7:00 PM - 8:00 PM",
        pickupCountdown = "Pickup starts in 17h 59m",
        price = 5.90,
        quantity = 1,
        address = "12, Jalan Bukit Bintang, KL",
        distance = "1.2 km away",
        code = "A7X92K"
    )

)

@Composable
fun MainApp(){
    val navController = rememberNavController()
    val isConsumer = false

    Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = { AppNavigationBar(navController, isConsumer) })
    { innerPadding ->
        //screen content
        NavHost(
            navController = navController,
            startDestination = "home"
        ) {
            composable("home"){
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
            composable("add"){
                AddFoodScreen(innerPadding = innerPadding, navController = navController)
            }
            composable("order"){
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

            composable("inventory"){
                InventoryScreen(innerPadding = innerPadding)
            }
            composable("profile"){
                ProfileScreen(innerPadding = innerPadding)
            }
        }
    }
}

@Composable
fun ProviderOrderScreen(innerPadding: PaddingValues, navController: NavController) {
    val orders = listOf(
        Reservation("RSV-001", R.drawable.nasi_lemak, "Nasi Lemak Combo", "Daniel", "6:00 PM", "", 8.0, 2, "", "", "A7X92K"),
        Reservation("RSV-002", R.drawable.bakery, "Assorted Bread", "Sarah", "7:30 PM", "", 12.0, 1, "", "", "B9Y12Z")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Text("Orders", fontSize = 24.sp, fontWeight = FontWeight.Bold) }
        items(orders) { order ->
            ProviderOrderCard(order)
        }
    }
}
@Composable
fun ProviderOrderCard(order: Reservation) {
    var completed by remember { mutableStateOf(order.isCompleted) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(order.imageResId),
                    contentDescription = null,
                    modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(order.foodName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Code: ${order.code}", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = PrimaryGreen)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Qty: ${order.quantity}", fontSize = 12.sp, color = Color.Gray)
                        Text("Pickup: ${order.pickupTimeRange}", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { completed = true },
                enabled = !completed, // 只有未完成时可点击
                modifier = Modifier.fillMaxWidth().height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (completed) Color(0xFFF1F5F9) else PrimaryGreen,
                    disabledContainerColor = Color(0xFFF1F5F9)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (completed) "Completed" else "Mark as Done",
                    color = if (completed) Color.Gray else Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
@Composable
fun ProviderNotificationScreen(innerPadding: PaddingValues) {
    val providerNotifications = listOf(
        NotificationItem("New order! Order #RSV-001 pickup starts in 30 mins.", "10 mins ago", R.drawable.alarm_24dp_2854c5_fill0_wght400_grad0_opsz24, SecondaryBlue, PrimaryBlue),
        NotificationItem("Order #RSV-002 pickup window is ending soon.", "1 hour ago", R.drawable.warning_24dp_f19e39_fill0_wght400_grad0_opsz24, SecondaryYellow, PrimaryYellow),
        NotificationItem("Low stock alert! You have less than 3 items for 'Kaya Bun'.", "3 hours ago", R.drawable.add_circle_24dp_e3e3e3_fill0_wght400_grad0_opsz24, SafeColor, PrimaryGreen)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(innerPadding)
            .padding(16.dp)
    ) {
        Text(
            text = "Notifications",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryTextColor,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(providerNotifications) { notification ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = notification.containerColor,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    painter = painterResource(notification.iconResId),
                                    contentDescription = null,
                                    tint = notification.iconTint,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = notification.title,
                                fontSize = 14.sp,
                                color = PrimaryTextColor,
                                lineHeight = 20.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = notification.timeAgo,
                                fontSize = 12.sp,
                                color = SecondaryTextColor
                            )
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun ProfileScreen(innerPadding: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentAlignment = Alignment.Center
    ){
        Text("Profile Screen", fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun InventoryScreen(innerPadding: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentAlignment = Alignment.Center
    ){
        Text("Inventory Screen Coming Soon!", fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun HomeScreen(innerPadding: PaddingValues, navController: NavController){
    //Dummy data:
    //Nearby restaurant
    val restaurant = listOf(
        Restaurant(R.drawable.boulangeriebakery,"Boulangerie Bakery", "1.2 km away"),
        Restaurant(R.drawable.sushikaen,"Sushi Kaen", "2.5 km away"),
        Restaurant(R.drawable.gardencafe,"Garden Cafe", "3.1 km away")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(innerPadding)
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
                    Text(text = "FoodLoop", color = PrimaryGreen, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                    Text(text = "Reduce waste, save food \uD83C\uDF3F", fontSize = 14.sp, color = SecondaryGreen)
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
        //Nearby Restaurant Slider
        item {
            Text("Nearby Restaurants", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(restaurant){ rest ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp),
                        modifier = Modifier
                            .size(width = 140.dp, height = 150.dp)
                            .clickable{
                                navController.navigate("restaurant_detail")
                            }
                            .shadow(
                                elevation = 8.dp,  //shadow size
                                shape = RoundedCornerShape(12.dp),
                                spotColor = Color.Gray, //shadow color
                                ambientColor = Color.LightGray
                            )
                    ) {
                        Column {
                            Image(
                                painter = painterResource(rest.imageResId),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .height(90.dp)
                                    .fillMaxWidth()
                            )
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(
                                    rest.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = Color.Black
                                )
                                Text(
                                    text = rest.distance,
                                    color = Color.Gray,
                                    fontSize = 12.sp,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
        //Suggested Food separator
        item {
            Text("Suggested Food", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
            Spacer(modifier = Modifier.height(8.dp))

            val categories = listOf("All","Meals","Bakery","Snacks")
            var selectedCategory by remember { mutableStateOf(0) }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories.size){ index ->
                    val isSelected = selectedCategory == index
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) PrimaryGreen else SafeColor,
                        modifier = Modifier.height(36.dp),
                        onClick = { selectedCategory = index}
                    ) {
                        Box(
                            contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)
                        ){
                            Text(
                                text = categories[index],
                                color = if (isSelected) BackgroundColor else SecondaryGreen,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        //food card list
        items(foodList){ food ->
            val finalPrice = food.getFinalPrice()

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clickable{
                        navController.navigate("food_detail/\$index")
                    },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column {
                    Image(
                        painter = painterResource(id=food.imageResId),
                        contentDescription = food.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = food.title,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B),
                                modifier = Modifier.weight(1f) //push tag to the right
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(shape = RoundedCornerShape(6.dp), color = food.getBadgeColor()) {
                                Text(
                                    text = "${food.discountPercentage}% OFF",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        if (food.description.isNotBlank()){
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = food.description,
                                fontSize = 13.sp,
                                color = SecondaryTextColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis //if context exceed display "..."
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "RM ${"%.2f".format(food.oriPrice)}",
                                fontSize = 13.sp,
                                color = Color(0xFF94A3B8),
                                textDecoration = TextDecoration.LineThrough //ori price crossed out
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "RM ${"%.2f".format(finalPrice)}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = SecondaryGreen
                            )

                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ){
                            Text("Pickup: ${food.timeLabel}",
                                fontSize = 11.sp,
                                color = Color.DarkGray,
                                fontWeight = FontWeight.Medium
                            )
                            Text("Only ${food.quantity} left",
                                fontSize = 11.sp,
                                color = Color(0xFFE53935),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun ProviderHomeScreen(innerPadding: PaddingValues, navController: NavController) {
    // Dummy Data for Provider
    val activeListings = listOf(
        ProviderFoodItem(R.drawable.kayabun, "Kaya Bun", 15, "5:00 PM - 7:00 PM", 12.00),
        ProviderFoodItem(R.drawable.chickfloss, "Chicken floss bun", 5, "8:00 PM - 9:30 PM", 15.00), // if quantity less than 5 trigger almost badge
        ProviderFoodItem(R.drawable.pandanbun, "Pandan bun", 0, "6:30 PM - 7:30 PM", 6.00) //0 --> trigger sold out badge
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(innerPadding)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. Header (Restaurant Info & Bell)
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shop Icon
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SafeColor,
                    modifier = Modifier.size(50.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(R.drawable.home_4_svgrepo_com),
                            contentDescription = "Shop",
                            tint = PrimaryGreen,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Shop Name
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Restaurant dashboard", fontSize = 12.sp, color = SecondaryTextColor)
                    Text(text = "Abang Lee Bakery", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryTextColor)
                }

                // Notification Bell
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.size(40.dp)
                ) {
                    IconButton(onClick = { navController.navigate("notifications") }) {
                        Icon(painter = painterResource(R.drawable.notifications_24dp_e3e3e3_fill0_wght400_grad0_opsz24), contentDescription = "Notification", tint = PrimaryGreen)
                    }
                }
            }
        }

        // 2. Banner (Today at a glance)
        item {
            Surface(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(text = "TODAY AT A GLANCE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.8f), letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Small surplus, big impact.", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Keep good food in the loop with every listing.", fontSize = 13.sp, color = Color.White.copy(alpha = 0.9f))
                }
            }
        }

        // 3. Stats Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(modifier = Modifier.weight(1f), title = "Food Listed", value = "8", iconRes = R.drawable.fridge_svgrepo_com)
                StatCard(modifier = Modifier.weight(1f), title = "Active Food", value = "3", iconRes = R.drawable.add_circle_24dp_e3e3e3_fill0_wght400_grad0_opsz24)
                StatCard(modifier = Modifier.weight(1f), title = "Reservations", value = "15", iconRes = R.drawable.order_svgrepo_com)
            }
        }

        // 4. Section Title
        item {
            Column {
                Text(text = "Manage your listings", fontSize = 12.sp, color = SecondaryTextColor)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Your Active Surplus Food", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryTextColor)
                    Text(
                        text = "+ Add",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen,
                        modifier = Modifier
                            .clickable { navController.navigate("add") }
                            .padding(4.dp)
                    )
                }
            }
        }

        // 5. Food List
        items(activeListings) { item ->
            val isSoldOut = item.availableCount <= 0
            var showDeleteDialog by remember { mutableStateOf(false) }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFFF1F5F9))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Box {
                            Image(
                                painter = painterResource(id = item.imageResId),
                                contentDescription = item.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            )
                            if (isSoldOut) {
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .background(Color.White.copy(alpha = 0.5f))
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {

                                Text(
                                    text = item.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSoldOut) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.width(2.dp))

                                val badgeColor = when {
                                    isSoldOut -> Color(0xFFF1F5F9)
                                    item.availableCount <= 5 -> SecondaryYellow
                                    else -> SafeColor
                                }
                                val badgeTextColor = when {
                                    isSoldOut -> Color.Gray
                                    item.availableCount <= 5 -> PrimaryYellow
                                    else -> MaterialTheme.colorScheme.primary
                                }
                                val badgeText = when {
                                    isSoldOut -> "Sold Out"
                                    item.availableCount <= 5 -> "Almost"
                                    else -> "Available"
                                }

                                Surface(shape = RoundedCornerShape(12.dp), color = badgeColor) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(badgeTextColor))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = badgeText, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = badgeTextColor)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))
                            val infoColor = if (isSoldOut) Color(0xFFCBD5E1) else MaterialTheme.colorScheme.onSecondary
                            Text(text = "Available: ${item.availableCount} left", fontSize = 12.sp, color = infoColor)
                            Text(text = "Pickup: ${item.pickupTime}", fontSize = 12.sp, color = infoColor)

                            Spacer(modifier = Modifier.height(6.dp))
                            val priceColor = if (isSoldOut) Color.Gray else MaterialTheme.colorScheme.primary
                            Text(text = "RM${"%.0f".format(item.price)}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = priceColor)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFF1F5F9))) // Divider
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SafeColor.copy(alpha = 0.5f),
                            modifier = Modifier.clickable { /* TODO: Edit Action */ }
                        ) {
                            Text(text = "Edit", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryGreen, modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp))
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFFEBEE),
                            modifier = Modifier.clickable { showDeleteDialog = true }
                        ) {
                            Text(text = "Delete", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F), modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp))
                        }
                    }
                }
            }
            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Delete Listing") },
                    text = { Text("Are you sure you want to delete '${item.title}'? This action cannot be undone.") },
                    confirmButton = {
                        Button(
                            onClick = {
                                // delete action
                                showDeleteDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Delete", color = MaterialTheme.colorScheme.background)
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showDeleteDialog = false }) {
                            Text("Cancel", color = MaterialTheme.colorScheme.background)
                        }
                    }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun StatCard(modifier: Modifier = Modifier, title: String, value: String, iconRes: Int) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(painter = painterResource(id = iconRes), contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, fontSize = 11.sp, color = SecondaryTextColor, lineHeight = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Black, color = PrimaryTextColor)
        }
    }
}

@Composable
fun FoodDetailScreen(innerPadding: PaddingValues, food: HomeFoodItem, onBlackClick:()-> Unit){
    val redBadgeColor = textErrorColor
    var quantity by remember { mutableStateOf(1) }
    var expanded by remember { mutableStateOf(false) }
    var selectedTime by remember { mutableStateOf("6:00 PM") }

    val timeOptions = listOf(food.timeLabel, "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .verticalScroll(rememberScrollState())
            .padding(bottom = innerPadding.calculateBottomPadding())
    ) {
        Box(modifier = Modifier.fillMaxWidth()){
            Image(
                painter = painterResource(id = food.imageResId),
                contentDescription = "Food Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )
        }
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            //Title and restaurant name
            Text(
                text = food.title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryTextColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Charlotte's Bakery",
                fontSize = 14.sp,
                color = SecondaryTextColor,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "RM ${"%.2f".format(food.getFinalPrice())}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PrimaryGreen
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "RM ${"%.2f".format(food.oriPrice)}",
                    fontSize = 14.sp,
                    color = SecondaryTextColor,
                    textDecoration = TextDecoration.LineThrough
                )
                Spacer(modifier = Modifier.weight(1f))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = redBadgeColor
                ) {
                    Text(
                        text = "${food.discountPercentage}% OFF",
                        color = BackgroundColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(32.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    InfoItem(title = "Quantity", value = "${food.quantity} left")
                }
                Box(modifier = Modifier.weight(1f)) {
                    InfoItem(title = "Pickup", value = food.timeLabel)
                }
                Box(modifier = Modifier.weight(1f)) {
                    InfoItem(title = "Distance", value = "1.2 km")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            //Map
            Text(
                text = "Restaurant Location",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryTextColor
            )
            Spacer(modifier = Modifier.height(12.dp))
            Image(
                painter = painterResource(id = R.drawable.fakemap),
                contentDescription = "Fake Map",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.height(8.dp))
            //address
            Row(verticalAlignment = Alignment.CenterVertically){
                Icon(
                    painter = painterResource(id = R.drawable.location_on_24dp_e3e3e3_fill0_wght400_grad0_opsz24),
                    contentDescription = "Location Pin",
                    tint = redBadgeColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "No. 12 Jalan SS15/4, Subang Jaya",
                    fontSize = 13.sp,
                    color = SecondaryTextColor
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            //Reservation Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Make a Reservation",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryTextColor
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Quantity:",
                            fontSize = 14.sp,
                            color = SecondaryTextColor,
                            modifier = Modifier.width(70.dp)
                        )
                        //minus button
                        IconButton(
                            onClick = {
                                if(quantity > 1) quantity--
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .background(SafeColor, shape = CircleShape)
                        ) {
                            Text("-", color = SecondaryGreen, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                        Text(
                            text = quantity.toString(),
                            color = PrimaryTextColor,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .width(40.dp)
                                .padding(horizontal = 4.dp)
                        )
                        //plus button
                        IconButton(
                            onClick = { quantity++ }, // 真实情况可能还要判断库存上限
                            modifier = Modifier
                                .size(36.dp)
                                .background(SafeColor, shape = CircleShape)
                        ) {
                            Text("+", color = PrimaryGreen, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Pickup:", fontSize = 14.sp, color = SecondaryTextColor, modifier = Modifier.width(60.dp))
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                                .clickable{expanded = true}
                                .padding(horizontal = 12.dp, vertical = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = selectedTime, fontSize = 14.sp, color = PrimaryTextColor)
                                Text(text = "▼", fontSize = 10.sp, color = Color.Gray) //fake dropdown arrow
                            }

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {

                                timeOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            selectedTime = option
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    //warning frame
                    Surface(
                        color = SecondaryYellow,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                            .border(
                                border = BorderStroke(1.dp, BorderYellow),
                                shape = RoundedCornerShape(8.dp)
                            )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.warning_24dp_f19e39_fill0_wght400_grad0_opsz24),
                                contentDescription = "Warning",
                                tint = PrimaryYellow,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Please confirm your reservation carefully. Once confirmed, the reservation cannot be cancelled.",
                                fontSize = 12.sp,
                                color = BorderYellow
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    //confirm button
                    Button(
                        onClick = {
                            //Soon
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SecondaryGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "Reserve Now", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = BackgroundColor)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
@Composable
fun InfoItem(title: String, value: String) {
    Surface(
        color = SafeColor.copy(alpha = 0.3f),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                border = BorderStroke(1.dp, SecondaryGreen.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)
        ) {
            Text(text = title, fontSize = 11.sp, color = SecondaryTextColor)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryTextColor)
        }
    }
}

@Composable
fun AddFoodScreen(innerPadding: PaddingValues, navController: NavController) {
    var foodName by remember { mutableStateOf("") }
    var categoryExpanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("Select Category") }
    var quantity by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var originalPrice by remember { mutableStateOf("") }
    var selectedDiscount by remember { mutableStateOf(30) }
    var showDialog by remember { mutableStateOf(false) }

    val categories = listOf("Meals", "Bakery", "Snacks")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(innerPadding)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Add Surplus Food",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = PrimaryTextColor
        )

        // Food Name
        FormField(
            label = "Food Name",
            value = foodName,
            onValueChange = { foodName = it },
            placeholder = "Enter food name"
        )

        // Category Dropdown
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Category",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryTextColor,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { categoryExpanded = true }
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp)), // 🔥 显式添加边框
                    shape = RoundedCornerShape(12.dp),
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledBorderColor = Color.Transparent,
                        disabledTextColor = if (selectedCategory == "Select Category") MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.background
                    ),
                    trailingIcon = {
                        Text("▼", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSecondary, modifier = Modifier.padding(end = 12.dp))
                    }
                )

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { categoryExpanded = true }
                )

                DropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                selectedCategory = cat
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }
        }

        // Quantity Available
        FormField(
            label = "Quantity Available",
            value = quantity,
            onValueChange = { quantity = it },
            placeholder = "e.g. 5"
        )

        // Description
        FormField(
            label = "Description (Optional)",
            value = description,
            onValueChange = { description = it },
            placeholder = "Optional",
            singleLine = false,
            minLines = 3
        )

        // Pickup Time Range
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Pickup Time Range",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryTextColor,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Start Time
                OutlinedTextField(
                    value = "06:00 PM",
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        unfocusedContainerColor = Color.White
                    ),
                    trailingIcon = {
                        Icon(painterResource(R.drawable.alarm_24dp_2854c5_fill0_wght400_grad0_opsz24), contentDescription = "Time", tint = Color.Gray, modifier = Modifier.size(18.dp))
                    }
                )
                Text(text = "to", color = SecondaryTextColor, fontSize = 14.sp)
                // End Time
                OutlinedTextField(
                    value = "08:00 PM",
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        unfocusedContainerColor = Color.White
                    ),
                    trailingIcon = {
                        Icon(painterResource(R.drawable.alarm_24dp_2854c5_fill0_wght400_grad0_opsz24), contentDescription = "Time", tint = Color.Gray, modifier = Modifier.size(18.dp))
                    }
                )
            }
        }

        // doted line frame (food upload)
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Food Image",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryTextColor,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            val stroke = Stroke(width = 3f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f))
            val greenColor = PrimaryGreen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .drawBehind() { drawRoundRect(color = greenColor.copy(alpha = 0.4f), style = stroke, cornerRadius = CornerRadius(12.dp.toPx())) }
                    .background(SafeColor.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                    .clickable { /* Handle Image Upload */ },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(R.drawable.add_circle_24dp_e3e3e3_fill0_wght400_grad0_opsz24),
                        contentDescription = "Upload",
                        tint = PrimaryGreen,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Tap to upload image", color = PrimaryGreen, fontSize = 13.sp)
                }
            }
        }

        FormField(
            label = "Original Price (RM)",
            value = originalPrice,
            onValueChange = { originalPrice = it },
            placeholder = "RM 0.00"
        )

        FormField(
            label = "Discount Percentage (%)",
            value = if (selectedDiscount == 0) "" else selectedDiscount.toString(),
            onValueChange = { input ->
                val numericValue = input.filter { it.isDigit() }.toIntOrNull() ?: 0
                selectedDiscount = if (numericValue > 100) 100 else numericValue
            },
            placeholder = "e.g. 50"
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { showDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Publish Food", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.background)
        }
        if (showDialog) {
            AlertDialog(
                 onDismissRequest = { showDialog = false },
                 title = { Text("Confirm Publish") },
                 text = { Text("Are you sure you want to publish this surplus food?") },
                 confirmButton = {
                     Button(
                         onClick = {
                             showDialog = false
                             // back to home
                             navController.navigate("home") {
                                 popUpTo("home") { inclusive = true }
                             }
                         }
                     ) { Text("Confirm", color = MaterialTheme.colorScheme.background) }
                 },
                 dismissButton = {
                     Button(onClick = { showDialog = false }) { Text("Cancel",color = MaterialTheme.colorScheme.background) }
                 }
             )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryTextColor,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(text = placeholder, color = Color.Gray, fontSize = 14.sp) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFE2E8F0),
                focusedBorderColor = PrimaryGreen,
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            ),
            singleLine = singleLine,
            minLines = minLines
        )
    }
}

@Composable
fun OrderScreen(innerPadding: PaddingValues, navController: NavController) {
    // 这里直接使用全局的 reservationsList，不需要再定义一个局部的 reservations 变量
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(innerPadding)
            .padding(16.dp)
    ) {
        Text(
            text = "My Reservations",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 确保这里用的是 reservationsList
            items(reservationsList.size) { index ->
                val reservation = reservationsList[index]
                ReservationCard(
                    reservation = reservation,
                    onClick = { navController.navigate("order_detail/$index") } // 传入正确的 index
                )
            }
        }
    }
}

@Composable
fun NotificationScreen(innerPadding: PaddingValues){
    val notifications = listOf(
        NotificationItem("Your milk will expire soon. Use it soon to prevent food waste!", "2 hours ago", R.drawable.warning_24dp_f19e39_fill0_wght400_grad0_opsz24, SecondaryYellow, PrimaryYellow),
        NotificationItem("Your Sushi Box pickup time is approaching. Don't forget!", "5 hours ago", R.drawable.alarm_24dp_2854c5_fill0_wght400_grad0_opsz24, SecondaryBlue, PrimaryBlue),
        NotificationItem("Congratulations! You saved 10 meals and earned the Food Saver badge!", "1 day ago", R.drawable.trophy_24dp_cccccc_fill0_wght400_grad0_opsz24, SafeColor, PrimaryGreen)
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(innerPadding)
            .padding(16.dp)
    ) {
        Text(
            text = "Notifications",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryTextColor,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        //Notification list
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(notifications) { notification ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = BackgroundColor),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = notification.containerColor,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    painter = painterResource(notification.iconResId),
                                    contentDescription = null,
                                    tint = notification.iconTint,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = notification.title,
                                fontSize = 14.sp,
                                color = PrimaryTextColor,
                                lineHeight = 20.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = notification.timeAgo,
                                fontSize = 12.sp,
                                color = SecondaryTextColor
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RestaurantDetailScreen(innerPadding: PaddingValues, navController: NavController) {
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(bottom = innerPadding.calculateBottomPadding()),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Image(
                    painter = painterResource(id = R.drawable.boulangeriebakery),
                    contentDescription = "Restaurant Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(230.dp)
                )
                //restaurant info
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Boulangerie Bakery", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onPrimary)
                    Spacer(modifier = Modifier.height(12.dp))
                    RestaurantInfoRow(iconResId = R.drawable.location_on_24dp_e3e3e3_fill0_wght400_grad0_opsz24, text = "No. 12, Jalan SS15/4, Subang Jaya")
                    RestaurantInfoRow(iconResId = R.drawable.directions_run_24dp_cccccc_fill0_wght400_grad0_opsz24, text = "1.2 km away")
                    RestaurantInfoRow(iconResId = R.drawable.alarm_24dp_2854c5_fill0_wght400_grad0_opsz24, text = "Pickup today: 6:30 PM - 7:30 PM")
                }
            }

            item {
                Spacer(modifier = Modifier.fillMaxWidth().height(8.dp).background(Color(0xFFF1F5F9)))
            }

            //available surplus food header
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Available Surplus Food", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                        Surface(shape = RoundedCornerShape(6.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                            Text(text = "${restaurantSpecificFoods.size} available", color = MaterialTheme.colorScheme.primary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                    Text(text = "Good food deserves a second chance.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSecondary, modifier = Modifier.padding(top = 4.dp))
                }
            }

            items(restaurantSpecificFoods.size) { index ->
                val food = restaurantSpecificFoods[index]
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp).clickable { navController.navigate("food_detail/$index") },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                    elevation = CardDefaults.cardElevation(0.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.2f))
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Image(painter = painterResource(id = food.imageResId), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp)))
                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = food.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Surface(shape = RoundedCornerShape(6.dp), color = food.getBadgeColor()) {
                                    Text(text = "${food.discountPercentage}% OFF", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                }
                            }
                            Text(text = food.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "RM ${"%.2f".format(food.getFinalPrice())}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "RM ${"%.2f".format(food.oriPrice)}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSecondary, textDecoration = TextDecoration.LineThrough)

                                Spacer(modifier = Modifier.weight(1f))

                                Text(text = "Only ${food.quantity} left", color = MaterialTheme.colorScheme.error, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                Icon(painter = painterResource(id = R.drawable.arrow_forward_24dp_cccccc_fill0_wght400_grad0_opsz24), contentDescription = null, tint = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun OrderDetailScreen(innerPadding: PaddingValues,navController: NavController, order: Reservation) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = innerPadding.calculateTopPadding())
                    .padding(horizontal = 4.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(painter = painterResource(R.drawable.arrow_back_ios_new_24dp_e3e3e3_fill0_wght400_grad0_opsz24), contentDescription = "Back")
                }
                Text(
                    text = "Reservation Details",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryTextColor
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(bottom = innerPadding.calculateBottomPadding())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = order.foodName,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                lineHeight = 25.sp
            )
            Text(
                text = order.restaurantName,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            //order info
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.05f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ReceiptRow("Order ID", order.orderId)
                    ReceiptRow("Quantity", "x${order.quantity}")
                    ReceiptRow("Total Price", "RM ${"%.2f".format(order.price)}")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "Pickup Details", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = order.address, fontSize = 14.sp)
            Text(text = order.pickupTimeRange, fontSize = 14.sp, fontWeight = FontWeight.Medium)

            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Location", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))

            Image(
                painter = painterResource(id = R.drawable.fakemap),
                contentDescription = "Map Location",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(text = "YOUR RESERVATION CODE", modifier = Modifier.align(Alignment.CenterHorizontally), color = MaterialTheme.colorScheme.onSecondary, fontSize = 12.sp)
            Surface(
                color = Color(0xFF1E293B),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Text(
                    text = order.code,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(20.dp),
                    letterSpacing = 8.sp
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ReceiptRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, color = MaterialTheme.colorScheme.onSecondary)
        Text(text = value, fontWeight = FontWeight.SemiBold)
    }
}
@Composable
fun AppNavigationBar(navController: NavController, isConsumer: Boolean ){
    var selectedItem by remember { mutableStateOf(if(isConsumer) "home" else "order") }

    NavigationBar(
        containerColor = Color.White,
        modifier = Modifier.shadow(16.dp, spotColor = Color.Gray, ambientColor = Color.Gray)
    ) {
        NavigationBarItem(
            selected = selectedItem == "home",
            onClick = {
                selectedItem = "home"
                navController.navigate("home"){
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.home_4_svgrepo_com),
                    contentDescription = "Home",
                    tint =  if(selectedItem == "home") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.size(26.dp)
                )
            },
            label = { Text("Home",style = MaterialTheme.typography.labelMedium)},
            colors = navigationItemColors()
        )

        //Order button
        NavigationBarItem(
            selected = selectedItem == "order",
            onClick = {
                selectedItem = "order"
                navController.navigate("order"){
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.order_svgrepo_com),
                    contentDescription = "order",
                    tint =  if(selectedItem == "order") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.size(26.dp)
                )
            },
            label = { Text(if(isConsumer) "My Order" else "Order",style = MaterialTheme.typography.labelMedium)},
            colors = navigationItemColors()
        )

        //Add button --> only for provider
        if(!isConsumer){
            NavigationBarItem(
                selected = selectedItem == "add",
                onClick = {
                    selectedItem = "add"
                    navController.navigate("add"){
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.add_circle_24dp_e3e3e3_fill0_wght400_grad0_opsz24),
                        contentDescription = "add",
                        tint = if(selectedItem == "add") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier.size(26.dp)
                    )
                },
                label = { Text("Add",style = MaterialTheme.typography.labelMedium)},
                colors = navigationItemColors()
            )
        } else {
            //Fridge button
            NavigationBarItem(
                selected = selectedItem == "fridge",
                onClick = {
                    selectedItem = "fridge"
                    navController.navigate("inventory"){
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(painter = painterResource(R.drawable.fridge_svgrepo_com),
                        contentDescription = "fridge",
                        tint =  if(selectedItem == "fridge") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier.size(26.dp)
                    )
                },
                label = { Text("Fridge",style = MaterialTheme.typography.labelMedium)},
                colors = navigationItemColors()
            )
        }

        //Profile button
        NavigationBarItem(
            selected = selectedItem == "profile",
            onClick = {
                selectedItem = "profile"
                navController.navigate("profile"){
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.account_circle_24dp_e3e3e3_fill0_wght400_grad0_opsz24),
                    contentDescription = "profile",
                    tint =  if(selectedItem == "profile") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.size(26.dp)
                )
            },
            label = { Text("Profile",style = MaterialTheme.typography.labelMedium)},
            colors = navigationItemColors()
        )
    }
}

//Data type declaration for food entity
//Home
data class Restaurant(
    val imageResId: Int, //tell the card which image to look for in the drawable resources
    val name: String,
    val distance: String
)
data class HomeFoodItem(
    val imageResId: Int,
    val title: String,
    val description: String,
    val oriPrice: Double,
    val quantity: Int,
    val timeLabel: String,
    val discountPercentage: Int
) {
    fun getFinalPrice(): Double {
        return oriPrice * (1.0 - (discountPercentage / 100.0))
    }
    fun getBadgeColor(): Color {
        return when {
            discountPercentage >= 70 -> Color(0xFFF44336)
            discountPercentage >= 50 -> Color(0xFFFF9800)
            else -> PrimaryGreen
        }
    }
}
data class NotificationItem(
    val title: String,
    val timeAgo: String,
    val iconResId: Int,
    val containerColor: Color,
    val iconTint: Color
)
data class Reservation(
    val orderId: String,
    val imageResId: Int,
    val foodName: String,
    val restaurantName: String,
    val pickupTimeRange: String,
    val pickupCountdown: String,
    val price: Double,
    val quantity: Int,
    val address: String,
    val distance: String,
    val code: String,
    var isCompleted: Boolean = false
)
data class ProviderFoodItem(
    val imageResId: Int,
    val title: String,
    val availableCount: Int,
    val pickupTime: String,
    val price: Double
)

@Composable
fun ReservationCard(reservation: Reservation, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = reservation.imageResId),
                    contentDescription = reservation.foodName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(70.dp).clip(RoundedCornerShape(12.dp))
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = reservation.foodName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = reservation.restaurantName, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSecondary)
                    Text(text = "Pickup: ${reservation.pickupTimeRange}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            // View Details Button
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth().height(40.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            ) {
                Text(text = "View Details", color = MaterialTheme.colorScheme.background, fontWeight = FontWeight.Bold)
            }
        }
    }
}
@Composable
fun RestaurantInfoRow(
    iconResId: Int,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(vertical = 6.dp)
    ) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

