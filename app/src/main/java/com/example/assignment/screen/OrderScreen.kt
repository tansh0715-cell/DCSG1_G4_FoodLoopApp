package com.example.assignment.screen
import com.example.assignment.model.FoodListing
import com.example.assignment.model.Restaurant
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.assignment.R
import com.example.assignment.components.ProviderOrderCard
import com.example.assignment.components.ReceiptRow
import com.example.assignment.model.Order
import com.example.assignment.viewmodel.OrderViewModel
import com.example.assignment.util.googleMapThumbnailUrl
import com.example.assignment.util.openGoogleMaps
@Composable
fun OrderScreen(
    innerPadding: PaddingValues,
    navController: NavController,
    orderViewModel: OrderViewModel
) {
    val orders by orderViewModel.orders.collectAsStateWithLifecycle()
    val isLoading by orderViewModel.isLoading.collectAsStateWithLifecycle()

    val foodsByOrderId by orderViewModel.foodByOrderId.collectAsStateWithLifecycle()

    val restaurantsByOrderId by orderViewModel.restaurantByOrderId.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        orderViewModel.loadConsumerOrders()
    }

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

        when {

            isLoading -> {
                Text("Loading reservations...")
            }

            orders.isEmpty() -> {
                Text(
                    text = "No reservations yet.",
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }

            else -> {

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    items(
                        items = orders,
                        key = { it.id }
                    ) { order ->
                        SaverReservationCard(
                            order = order,
                            food = foodsByOrderId[order.id],
                            restaurant = restaurantsByOrderId[order.id],
                            onClick = {
                                navController.navigate(
                                    "ORDER_DETAIL/${order.id}"
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProviderOrderScreen(
    innerPadding: PaddingValues,
    navController: NavController,
    orderViewModel: OrderViewModel
) {

    val orders by
    orderViewModel.orders.collectAsStateWithLifecycle()

    val isLoading by
    orderViewModel.isLoading.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        orderViewModel.loadProviderOrders()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item {

            Text(
                text = "Orders",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        if (isLoading) {

            item {

                Text(
                    text = "Loading orders..."
                )
            }

        } else if (orders.isEmpty()) {

            item {

                Text(
                    text = "No orders.",
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }

        } else {
            items(
                items = orders,
                key = { order -> order.id }
            ) { order ->

                ProviderOrderCard(
                    order = order,
                    onMarkDone = {
                        orderViewModel.markAsDone(
                            order.id
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun OrderDetailScreen(
    innerPadding: PaddingValues,
    navController: NavController,
    order: Order,
    orderViewModel: OrderViewModel
) {
    val context = LocalContext.current

    val food by
    orderViewModel.selectedFood.collectAsStateWithLifecycle()

    val restaurant by
    orderViewModel.selectedRestaurant.collectAsStateWithLifecycle()

    val isDetailLoading by
    orderViewModel.isDetailLoading.collectAsStateWithLifecycle()

    LaunchedEffect(order.id) {
        orderViewModel.loadOrderDetails(order)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            )
    ) {

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background,
            shadowElevation = 4.dp
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = innerPadding.calculateTopPadding()
                    )
                    .padding(
                        horizontal = 4.dp, vertical = 12.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = {
                        navController.popBackStack()
                    }
                ) {

                    Icon(
                        painter = painterResource(
                            R.drawable.arrow_back_ios_new_24dp_e3e3e3_fill0_wght400_grad0_opsz24
                        ),
                        contentDescription = "Back"
                    )
                }

                Text(
                    text = "Reservation Details",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(
                    bottom = innerPadding.calculateBottomPadding()
                )
                .padding(horizontal = 20.dp)
        ) {

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = food?.name?:"Food",
                style = MaterialTheme.typography.headlineLarge,
                lineHeight = 25.sp
            )

            Text(
                text = restaurant?.name ?: "Restaurant",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(
                    top = 4.dp,
                    bottom = 24.dp
                )
            )

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.onSecondary.copy(
                    alpha = 0.05f
                ),
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    ReceiptRow(
                        "Order ID",
                        order.id
                    )

                    ReceiptRow(
                        "Quantity",
                        "x${order.quantity}"
                    )

                    ReceiptRow(
                        "Total Price",
                        "RM ${"%.2f".format(order.totalPrice)}"
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Text(
                text = "Pickup Details",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = restaurant?.address ?: "Address unavailable",
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = order.pickupTime,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Text(
                text = "Location",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            restaurant?.let { currentRestaurant ->
                AsyncImage(
                    model = googleMapThumbnailUrl(
                        currentRestaurant.address
                    ),
                    contentDescription = "Restaurant location",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(
                            RoundedCornerShape(12.dp)
                        )
                        .border(
                            1.dp, MaterialTheme.colorScheme.onSecondary.copy(
                                alpha = 0.2f
                            ), RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            restaurant?.let {
                                openGoogleMaps(
                                    context, it
                                )
                            }
                        }
                )

                Spacer(
                    modifier = Modifier.height(32.dp)
                )

                Text(
                    text = "YOUR RESERVATION CODE",
                    modifier = Modifier.align(
                        Alignment.CenterHorizontally
                    ),
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontSize = 12.sp
                )

                Surface(
                    color = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {

                    Text(
                        text = order.pickupCode,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(20.dp),
                        letterSpacing = 8.sp
                    )
                }

                Spacer(
                    modifier = Modifier.height(32.dp)
                )
            }
        }
    }
}

@Composable
private fun SaverReservationCard(
    order: Order,
    food: FoodListing?,
    restaurant: Restaurant?,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                AsyncImage(
                    model = food?.imageUrl,
                    contentDescription = food?.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(
                            RoundedCornerShape(12.dp)
                        )
                )

                Spacer(
                    modifier = Modifier.width(16.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = food?.name ?: "Reservation",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Text(
                        text = restaurant?.name ?: "Restaurant",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSecondary
                    )

                    Text(
                        text = "Pickup: ${order.pickupTime}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = "Qty: ${order.quantity}",
                    fontSize = 12.sp
                )

                Text(
                    text = "RM ${"%.2f".format(order.totalPrice)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Button(
                onClick = onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("View Details")
            }
        }
    }
}