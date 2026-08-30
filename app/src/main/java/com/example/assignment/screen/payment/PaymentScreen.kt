package com.example.assignment.screen.payment

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment.model.Order
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun PaymentScreen(
    foodName: String,
    quantity: Int,
    total: Double,
    onPaymentSuccess: () -> Unit,
    onBack: () -> Unit
) {
    val items = listOf(
        Triple(
            foodName, total / quantity, quantity
        )
    )
    var isProcessing by remember { mutableStateOf(false) }
    var selectedPaymentMethod by remember { mutableStateOf("credit_card") }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "← Checkout",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0B1A33),
                    modifier = Modifier.clickable { onBack() }
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Your Order",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0B1A33),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    items.forEachIndexed { index, (name, price, qty) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = name,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF0B1A33)
                            )
                            Text(
                                text = "RM ${String.format(Locale.ENGLISH, "%.2f", price)} each",
                                fontSize = 14.sp,
                                color = Color(0xFF4A5A72)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "x $qty",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = "RM ${String.format(Locale.ENGLISH, "%.2f", price * qty)}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                        }
                        if (index < items.size - 1) {
                            HorizontalDivider(color = Color(0xFFDCE4EE), thickness = 1.dp)
                        }
                    }

                    HorizontalDivider(
                        color = Color(0xFFDCE4EE),
                        thickness = 2.dp,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0B1A33)
                        )
                        Text(
                            text = "RM ${String.format(Locale.ENGLISH, "%.2f", total)}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
            }

            OutlinedTextField(
                value = "012-3456789",
                onValueChange = {},
                label = { Text("Bind Account / Phone *") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                textStyle = TextStyle(color = Color.Black),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2E7D32),
                    unfocusedBorderColor = Color(0xFFDCE4EE),
                    disabledBorderColor = Color(0xFFDCE4EE)
                ),
                readOnly = true
            )
            Text(
                text = "Used for payment verification & order tracking",
                fontSize = 12.sp,
                color = Color(0xFF8A9BB5),
                modifier = Modifier.padding(start = 4.dp)
            )

            Text(
                text = "Payment Method",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0B1A33),
                modifier = Modifier.padding(top = 4.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(0.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PaymentMethodChip(
                        text = "Credit Card",
                        isSelected = selectedPaymentMethod == "credit_card",
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedPaymentMethod = "credit_card" }
                    )
                    PaymentMethodChip(
                        text = "Touch 'n Go",
                        isSelected = selectedPaymentMethod == "tng",
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedPaymentMethod = "tng" }
                    )
                }
            }

            when (selectedPaymentMethod) {
                "credit_card" -> {
                    CreditCardForm()
                }
                "tng" -> {
                    TNGPaymentCard()
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(16.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🛡️", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Your payment is secured with 256-bit encryption.",
                    fontSize = 13.sp,
                    color = Color(0xFF2A3A50)
                )
            }

            val buttonColor = if (selectedPaymentMethod == "tng") {
                Color(0xFF0056B3)
            } else {
                Color(0xFF2E7D32)
            }

            val buttonText = if (selectedPaymentMethod == "tng") {
                "Pay with TNG RM ${String.format(Locale.ENGLISH, "%.2f", total)}"
            } else {
                "Pay RM ${String.format(Locale.ENGLISH, "%.2f", total)}"
            }

            Button(
                onClick = {
                    if (!isProcessing) {
                        coroutineScope.launch {
                            isProcessing = true
                            delay(2000)
                            isProcessing = false
                            onPaymentSuccess()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                enabled = !isProcessing
            ) {
                Text(
                    text = if (isProcessing) "Processing..." else buttonText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Text(
                text = "By clicking you agree to our Terms & Privacy Policy",
                fontSize = 12.sp,
                color = Color(0xFF8A9BB5),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}





