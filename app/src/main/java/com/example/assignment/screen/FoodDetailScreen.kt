package com.example.assignment.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment.R
import com.example.assignment.components.InfoItem
import com.example.assignment.model.HomeFoodItem
import com.example.assignment.ui.theme.BorderYellow
import com.example.assignment.ui.theme.SafeColor
import com.example.assignment.ui.theme.SecondaryGreen
import com.example.assignment.ui.theme.SecondaryYellow

@Composable
fun FoodDetailScreen(innerPadding: PaddingValues, food: HomeFoodItem, onBackClick:()-> Unit){
    val redBadgeColor = MaterialTheme.colorScheme.error
    var quantity by remember { mutableStateOf(1) }
    var expanded by remember { mutableStateOf(false) }
    var selectedTime by remember { mutableStateOf("6:00 PM") }

    val timeOptions = listOf(food.timeLabel, "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Charlotte's Bakery",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSecondary,
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
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "RM ${"%.2f".format(food.oriPrice)}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSecondary,
                    textDecoration = TextDecoration.LineThrough
                )
                Spacer(modifier = Modifier.weight(1f))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = redBadgeColor
                ) {
                    Text(
                        text = "${food.discountPercentage}% OFF",
                        color = MaterialTheme.colorScheme.background,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(32.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 20.dp)
                    .height(IntrinsicSize.Min),
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
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary
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
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            //Reservation Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Make a Reservation",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Quantity:",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSecondary,
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
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .width(40.dp)
                                .padding(horizontal = 4.dp)
                        )
                        //plus button
                        IconButton(
                            onClick = { quantity++ },
                            modifier = Modifier
                                .size(36.dp)
                                .background(MaterialTheme.colorScheme.primaryContainer, shape = CircleShape)
                        ) {
                            Text("+", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Pickup:", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSecondary, modifier = Modifier.width(60.dp))
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                                .clickable{expanded = true}
                                .padding(horizontal = 12.dp, vertical = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = selectedTime, fontSize = 14.sp, color = MaterialTheme.colorScheme.onPrimary)
                                Text(text = "▼", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSecondary) //fake dropdown arrow
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
                                tint = MaterialTheme.colorScheme.tertiary,
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
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "Reserve Now", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.background)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}