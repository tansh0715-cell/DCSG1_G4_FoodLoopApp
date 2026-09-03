package com.example.assignment.screen.food

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.assignment.R
import com.example.assignment.components.InfoItem
import com.example.assignment.model.FoodListing
import com.example.assignment.model.FoodStatus
import com.example.assignment.model.Restaurant
import com.example.assignment.model.getDiscountBadgeColor
import com.example.assignment.util.mapboxStaticMapUrl
import com.example.assignment.util.openGoogleMaps

@Composable
fun FoodDetailScreen(
    innerPadding: PaddingValues,
    restaurant: Restaurant? = null,
    food: FoodListing,
    onBackClick:()-> Unit,
    onPurchase: (String, Int) -> Unit
) {
    val isSoldOut = food.status == FoodStatus.SOLD_OUT
    var reservationQuantity by remember { mutableStateOf(1) }
    val context = LocalContext.current
    var descriptionExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(bottom = innerPadding.calculateBottomPadding())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = food.imageUrl,
                contentDescription = food.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
                    .background(
                        Color.White.copy(alpha = 0.85f),
                        RoundedCornerShape(50)
                    )
            ) {
                Icon(
                    painter = painterResource(
                        id = R.drawable.arrow_back_24dp_e3e3e3_fill0_wght400_grad0_opsz24
                    ),
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            //Title and restaurant name
            Text(
                text = food.name,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = restaurant?.name ?: "Restaurant",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSecondary,
                fontWeight = FontWeight.Medium
            )
            if (!food.description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = food.description,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Justify,
                    color = MaterialTheme.colorScheme.onSecondary,
                    maxLines = if (descriptionExpanded) {
                        Int.MAX_VALUE
                    } else {
                        3
                    },
                    overflow = TextOverflow.Ellipsis
                )

                if (food.description.length > 180) {

                    Text(
                        text = if (descriptionExpanded) {
                            "Less"
                        } else {
                            "More"
                        },
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier
                            .clickable {
                                descriptionExpanded = !descriptionExpanded
                            }
                            .padding(top = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "RM ${"%.2f".format(food.price)}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                if (food.discountPercentage > 0) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "RM ${"%.2f".format(food.originalPrice)}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSecondary,
                        textDecoration = TextDecoration.LineThrough
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = food.getDiscountBadgeColor()
                    ) {
                        Text(
                            text = "${food.discountPercentage}% OFF",
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 11.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    InfoItem(title = "Quantity", value = "${food.quantity} left")
                }
                Box(modifier = Modifier.weight(1f)) {
                    InfoItem(title = "Pickup", value = food.pickupTime)
                }
                Box(modifier = Modifier.weight(1f)) {
                    InfoItem(title = "Category", value = food.category)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Restaurant Location",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (restaurant != null) {

                    AsyncImage(
                        model = mapboxStaticMapUrl(
                            context = context,
                            longitude = restaurant.longitude,
                            latitude = restaurant.latitude,
                            zoom = 15,
                            width = 800,
                            height = 400
                        ),
                        contentDescription = "Restaurant location",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(
                                RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                openGoogleMaps(
                                    context, restaurant
                                )
                            }
                    )

                } else {
                    Text(
                        text = "Restaurant location is unavailable.",
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontSize = 13.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.location_on_24dp_e3e3e3_fill0_wght400_grad0_opsz24),
                    contentDescription = "Location Pin",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = restaurant?.address ?: "Restaurant",
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
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
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

                        //Minus Button
                        IconButton(
                            enabled = !isSoldOut && reservationQuantity > 1,
                            onClick = {
                                if (reservationQuantity > 1) {
                                    reservationQuantity--
                                }
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                        ) {
                            Text(
                                "-",
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }

                        Text(
                            text = reservationQuantity.toString(),
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .width(40.dp)
                                .padding(horizontal = 6.dp)
                        )

                        IconButton(
                            enabled = !isSoldOut && reservationQuantity < food.quantity,
                            onClick = {
                                if (reservationQuantity < food.quantity) reservationQuantity++
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                        ) {
                            Text(
                                "+",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Pickup: ${food.pickupTime}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    //warning frame
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                border = BorderStroke(
                                    1.dp, MaterialTheme.colorScheme.onTertiaryContainer
                                ), shape = RoundedCornerShape(8.dp)
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
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        enabled = !isSoldOut && reservationQuantity >= 1 && reservationQuantity <= food.quantity,

                        onClick = {
                            if (reservationQuantity >= 1 && reservationQuantity <= food.quantity) {
                                onPurchase(
                                    food.id, reservationQuantity
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (isSoldOut) "Sold Out" else "Reserve Now",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.background
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}