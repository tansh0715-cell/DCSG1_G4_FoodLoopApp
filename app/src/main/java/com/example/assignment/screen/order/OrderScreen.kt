package com.example.assignment.screen.order

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.assignment.R
import com.example.assignment.components.ProviderOrderCard
import com.example.assignment.components.ReceiptRow
import com.example.assignment.components.SaverOrderCard
import com.example.assignment.model.Order
import com.example.assignment.viewmodel.order.OrderViewModel
import com.example.assignment.util.mapboxStaticMapUrl
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
                        SaverOrderCard (
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

    val orders by orderViewModel.orders.collectAsStateWithLifecycle()

    val foodsByOrderId by orderViewModel.foodByOrderId.collectAsStateWithLifecycle()

    var selectedOrder by remember {
        mutableStateOf<Order?>(null)
    }
    var showPickupDialog by remember {
        mutableStateOf(false)
    }
    var pickupError by remember {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(Unit) {
        while (true) {
            orderViewModel.loadConsumerOrders()

            // Re-check every 30 seconds
            kotlinx.coroutines.delay(30_000)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            )
            .padding(innerPadding)
            .padding(16.dp)
    ) {

        // Header
        Text(
            text = "Orders",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(
                bottom = 16.dp
            )
        )

        if (orders.isEmpty()) {

            Text(
                text = "No orders yet.",
                color = MaterialTheme.colorScheme.onSecondary,
                fontSize = 14.sp
            )

        } else {

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                items(
                    items = orders,
                    key = { order -> order.id }
                ) { order ->

                    ProviderOrderCard(
                        food = foodsByOrderId[order.id],
                        order = order,
                        onMarkDone = {

                            selectedOrder = order
                            pickupError = null
                            showPickupDialog = true
                        }
                    )
                }
            }
        }
    }

    if (
        showPickupDialog &&
        selectedOrder != null
    ) {
        PickupCodeDialog(
            order = selectedOrder!!,
            errorMessage = pickupError,
            onDismiss = {
                showPickupDialog = false
                selectedOrder = null
            },

            onConfirm = { code ->

                orderViewModel.markAsDone(

                    orderId = selectedOrder!!.id,
                    pickupCode = code,
                    onSuccess = {
                        showPickupDialog = false
                        selectedOrder = null
                        pickupError = null

                        orderViewModel.loadProviderOrders()
                    },

                    onError = { error ->
                        pickupError =
                            error.message ?: "Unable to complete order."
                    }
                )
            }
        )
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
                        order.orderCode
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
                    model = mapboxStaticMapUrl(
                        context = context,
                        longitude = currentRestaurant.longitude,
                        latitude = currentRestaurant.latitude,
                        zoom = 15,
                        width = 800,
                        height = 400
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
private fun PickupCodeDialog(
    order: Order,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {

    var pickupCode by remember {
        mutableStateOf("")
    }

    AlertDialog(
        onDismissRequest =
            onDismiss,
        title = {
            Text(
                text = "Complete Order",
                fontWeight =
                    FontWeight.Bold
            )
        },

        text = {
            Column {
                Text(
                    text =
                        "Ask the customer for their pickup code."
                )
                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )
                OutlinedTextField(
                    value = pickupCode,
                    onValueChange = {
                        pickupCode = it
                    },
                    label = {
                        Text(
                            "Pickup Code"
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (
                    errorMessage != null
                ) {
                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }
            }
        },

        confirmButton = {

            Button(
                onClick = {
                    val code = pickupCode.trim().uppercase()
                    if (code.isNotBlank()) {
                        onConfirm(code)
                    }
                }
            ) {
                Text("Confirm")
            }
        },

        dismissButton = {

            TextButton(
                onClick =
                    onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}