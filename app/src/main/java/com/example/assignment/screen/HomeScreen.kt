package com.example.assignment.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.assignment.R
import com.example.assignment.components.StatCard
import com.example.assignment.data.foodList
import com.example.assignment.model.ProviderFoodItem
import com.example.assignment.model.Restaurant
import com.example.assignment.model.HomeFoodItem
import com.example.assignment.ui.theme.BackgroundColor
import com.example.assignment.ui.theme.PrimaryGreen
import com.example.assignment.ui.theme.SafeColor
import com.example.assignment.ui.theme.SecondaryGreen
import com.example.assignment.viewmodel.HomeViewModel

@Composable
fun HomeScreen(innerPadding: PaddingValues, navController: NavController, viewModel: HomeViewModel = viewModel() ){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    //Dummy data:
    //Nearby restaurant
    val restaurant = listOf(
        Restaurant(R.drawable.boulangeriebakery, "Boulangerie Bakery", "1.2 km away"),
        Restaurant(R.drawable.sushikaen,"Sushi Kaen", "2.5 km away"),
        Restaurant(R.drawable.gardencafe,"Garden Cafe", "3.1 km away")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                    Text(text = "FoodLoop", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.displaySmall)
                    Text(text = "Reduce waste, save food \uD83C\uDF3F", fontSize = 14.sp, color = MaterialTheme.colorScheme.secondary)
                }

                Box{
                    IconButton(
                        onClick = {navController.navigate("notifications")},
                        modifier = Modifier
                            .size(36.dp)
                            .background(MaterialTheme.colorScheme.surface, CircleShape)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.notifications_24dp_e3e3e3_fill0_wght400_grad0_opsz24),
                            contentDescription = "Notification",
                            tint = MaterialTheme.colorScheme.primary
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
            Text("Nearby Restaurants", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onPrimary)
            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(restaurant){ rest ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(2.dp),
                        modifier = Modifier
                            .size(width = 140.dp, height = 150.dp)
                            .clickable{
                                navController.navigate("restaurant_detail")
                            }
                            .shadow(
                                elevation = 8.dp,  //shadow size
                                shape = RoundedCornerShape(12.dp),
                                spotColor = MaterialTheme.colorScheme.onSecondary, //shadow color
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
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Text(
                                    text = rest.distance,
                                    color = MaterialTheme.colorScheme.onSecondary,
                                    style = MaterialTheme.typography.bodyMedium,
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
            Text("Suggested Food", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onPrimary)
            Spacer(modifier = Modifier.height(8.dp))

            val categories = listOf("All","Meals","Bakery","Snacks")
            var selectedCategory by remember { mutableStateOf(0) }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories.size){ index ->
                    val isSelected = uiState.selectedCategoryIndex == index
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) PrimaryGreen else SafeColor,
                        modifier = Modifier.height(36.dp),
                        onClick = { viewModel.onCategorySelected(index)}
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
        items(foodList){ food: HomeFoodItem ->
            val finalPrice = food.getFinalPrice()

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clickable{
                        navController.navigate("food_detail/\$index")
                    },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                                color = MaterialTheme.colorScheme.onPrimary,
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
                                color = MaterialTheme.colorScheme.onSecondary,
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
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondary,
                                textDecoration = TextDecoration.LineThrough //ori price crossed out
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "RM ${"%.2f".format(finalPrice)}",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.secondary
                            )

                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ){
                            Text("Pickup: ${food.timeLabel}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondary,
                                fontWeight = FontWeight.Medium
                            )
                            Text("Only ${food.quantity} left",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.error,
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
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(50.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(R.drawable.home_4_svgrepo_com),
                            contentDescription = "Shop",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Shop Name
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Restaurant dashboard", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSecondary)
                    Text(text = "Abang Lee Bakery", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onPrimary)
                }

                // Notification Bell
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.size(40.dp)
                ) {
                    IconButton(onClick = { navController.navigate("notifications") }) {
                        Icon(painter = painterResource(R.drawable.notifications_24dp_e3e3e3_fill0_wght400_grad0_opsz24), contentDescription = "Notification", tint = MaterialTheme.colorScheme.primary)
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
                Text(text = "Manage your listings", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSecondary)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Your Active Surplus Food", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onPrimary)
                    Text(
                        text = "+ Add",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
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
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(1.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
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
                                    isSoldOut -> MaterialTheme.colorScheme.outline
                                    item.availableCount <= 5 -> MaterialTheme.colorScheme.surfaceVariant
                                    else -> MaterialTheme.colorScheme.primaryContainer
                                }
                                val badgeTextColor = when {
                                    isSoldOut -> MaterialTheme.colorScheme.onSecondary
                                    item.availableCount <= 5 -> MaterialTheme.colorScheme.tertiary
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
                                        Text(text = badgeText, style = MaterialTheme.typography.labelSmall, color = badgeTextColor)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))
                            val infoColor = if (isSoldOut) Color(0xFFCBD5E1) else MaterialTheme.colorScheme.onSecondary
                            Text(text = "Available: ${item.availableCount} left", fontSize = 12.sp, color = infoColor)
                            Text(text = "Pickup: ${item.pickupTime}", fontSize = 12.sp, color = infoColor)

                            Spacer(modifier = Modifier.height(6.dp))
                            val priceColor = if (isSoldOut) Color.Gray else MaterialTheme.colorScheme.primary
                            Text(text = "RM${"%.0f".format(item.price)}", style = MaterialTheme.typography.titleLarge, color = priceColor)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.outline)) // Divider
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            modifier = Modifier.clickable { /* TODO: Edit Action */ }
                        ) {
                            Text(text = "Edit", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp))
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.errorContainer,
                            modifier = Modifier.clickable { showDeleteDialog = true }
                        ) {
                            Text(text = "Delete", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp))
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

