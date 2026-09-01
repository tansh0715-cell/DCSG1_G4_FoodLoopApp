package com.example.assignment.screen.payment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun CreditCardForm(
    modifier: Modifier = Modifier
) {
    var cardNumber by remember { mutableStateOf("") }
    var expiry by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var cardHolderName by remember { mutableStateOf("") }

    fun formatCardNumber(input: String): String {
        val digits = input.filter { it.isDigit() }
        return digits.chunked(4).joinToString(" ").take(19)
    }

    fun formatExpiry(input: String): String {
        val digits = input.filter { it.isDigit() }
        return when {
            digits.length > 2 -> "${digits.take(2)}/${digits.drop(2).take(2)}"
            else -> digits
        }.take(5)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = cardNumber,
            onValueChange = { newValue ->
                val digits = newValue.filter { it.isDigit() }.take(16)
                cardNumber = formatCardNumber(digits)
            },
            label = { Text("Card Number *") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2E7D32),
                unfocusedBorderColor = Color(0xFFDCE4EE)
            )
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = expiry,
                onValueChange = { newValue ->
                    val digits = newValue.filter { it.isDigit() }.take(4)
                    expiry = formatExpiry(digits)
                },
                label = { Text("Expiry (MM/YY) *") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2E7D32),
                    unfocusedBorderColor = Color(0xFFDCE4EE)
                )
            )
            OutlinedTextField(
                value = cvv,
                onValueChange = { newValue ->
                    cvv = newValue.filter { it.isDigit() }.take(3)
                },
                label = { Text("CVV *") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2E7D32),
                    unfocusedBorderColor = Color(0xFFDCE4EE)
                )
            )
        }

        OutlinedTextField(
            value = cardHolderName,
            onValueChange = { cardHolderName = it.uppercase() },
            label = { Text("Cardholder Name *") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2E7D32),
                unfocusedBorderColor = Color(0xFFDCE4EE)
            )
        )
    }
}