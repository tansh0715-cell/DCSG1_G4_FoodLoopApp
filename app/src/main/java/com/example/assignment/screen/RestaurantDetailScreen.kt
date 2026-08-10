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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.assignment.R
import com.example.assignment.components.RestaurantInfoRow
import com.example.assignment.data.restaurantSpecificFoods
import com.example.assignment.model.HomeFoodItem

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
                    Text(text = "Boulangerie Bakery", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onPrimary)
                    Spacer(modifier = Modifier.height(12.dp))
                    RestaurantInfoRow(iconResId = R.drawable.location_on_24dp_e3e3e3_fill0_wght400_grad0_opsz24, text = "No. 12, Jalan SS15/4, Subang Jaya")
                    RestaurantInfoRow(iconResId = R.drawable.directions_run_24dp_cccccc_fill0_wght400_grad0_opsz24, text = "1.2 km away")
                    RestaurantInfoRow(iconResId = R.drawable.alarm_24dp_2854c5_fill0_wght400_grad0_opsz24, text = "Pickup today: 6:30 PM - 7:30 PM")
                }
            }

            item {
                Spacer(modifier = Modifier.fillMaxWidth().height(8.dp).background(MaterialTheme.colorScheme.outline))
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
                val food: HomeFoodItem = restaurantSpecificFoods[index]
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
                                Text(text = food.title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Surface(shape = RoundedCornerShape(6.dp), color = food.getBadgeColor()) {
                                    Text(text = "${food.discountPercentage}% OFF", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                }
                            }
                            Text(text = food.description, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "RM ${"%.2f".format(food.getFinalPrice())}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "RM ${"%.2f".format(food.oriPrice)}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSecondary, textDecoration = TextDecoration.LineThrough)

                                Spacer(modifier = Modifier.weight(1f))

                                Text(text = "Only ${food.quantity} left", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                                Icon(painter = painterResource(id = R.drawable.arrow_forward_24dp_cccccc_fill0_wght400_grad0_opsz24), contentDescription = null, tint = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}