package com.example.assignment.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.assignment.R
import com.example.assignment.components.ProviderOrderCard
import com.example.assignment.components.ReceiptRow
import com.example.assignment.components.ReservationCard
import com.example.assignment.data.reservationsList
import com.example.assignment.model.Reservation

@Composable
fun OrderScreen(innerPadding: PaddingValues, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(innerPadding)
            .padding(16.dp)
    ) {
        Text(
            text = "My Reservations",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
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
fun ProviderOrderScreen(innerPadding: PaddingValues, navController: NavController) {
    val orders = listOf(
        Reservation(
            "RSV-001",
            R.drawable.nasi_lemak,
            "Nasi Lemak Combo",
            "Daniel",
            "6:00 PM",
            "",
            8.0,
            2,
            "",
            "",
            "A7X92K"
        ),
        Reservation("RSV-002", R.drawable.bakery, "Assorted Bread", "Sarah", "7:30 PM", "", 12.0, 1, "", "", "B9Y12Z")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Text("Orders", style = MaterialTheme.typography.headlineMedium) }
        items(orders) { order ->
            ProviderOrderCard(order)
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
                    color = MaterialTheme.colorScheme.onPrimary
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
                style = MaterialTheme.typography.headlineLarge,
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
            Text(text = order.address, style = MaterialTheme.typography.bodyLarge)
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
                color = MaterialTheme.colorScheme.onPrimary,
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