package com.example.assignment.screen.payment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun CreditCardForm() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = "1234 5678 9012 3456",
            onValueChange = {},
            label = { Text("Card Number *") },
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
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = "12/26",
                onValueChange = {},
                label = { Text("Expiry *") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                textStyle = TextStyle(color = Color.Black),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2E7D32),
                    unfocusedBorderColor = Color(0xFFDCE4EE),
                    disabledBorderColor = Color(0xFFDCE4EE)
                ),
                readOnly = true
            )
            OutlinedTextField(
                value = "123",
                onValueChange = {},
                label = { Text("CVV *") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                textStyle = TextStyle(color = Color.Black),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2E7D32),
                    unfocusedBorderColor = Color(0xFFDCE4EE),
                    disabledBorderColor = Color(0xFFDCE4EE)
                ),
                readOnly = true
            )
        }
        OutlinedTextField(
            value = "AHMAD BIN ABDULLAH",
            onValueChange = {},
            label = { Text("Cardholder Name *") },
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
    }
}