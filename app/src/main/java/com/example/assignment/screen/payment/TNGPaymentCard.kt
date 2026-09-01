package com.example.assignment.screen.payment

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TNGPaymentCard(
    otp: String,
    onOtpChange: (String) -> Unit,
    onSendOtp: () -> Unit
) {
    var countdown by remember { mutableStateOf(60) }
    var isSending by remember { mutableStateOf(false) }
    var canResend by remember { mutableStateOf(false) }
    var otpSent by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        isSending = true
        delay(1500)
        isSending = false
        otpSent = true
        countdown = 60
        canResend = false
        onSendOtp()
    }

    LaunchedEffect(countdown) {
        if (countdown > 0 && !canResend) {
            delay(1000)
            countdown--
        } else if (countdown == 0) {
            canResend = true
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F7FF)),
        border = BorderStroke(2.dp, Color(0xFF0056B3))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(Color(0xFF0056B3), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("TNG", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("Touch 'n Go eWallet", fontWeight = FontWeight.Bold, color = Color(0xFF0B1A33))

            when {
                isSending -> {
                    Text(
                        text = "Sending OTP to your phone...",
                        fontSize = 13.sp,
                        color = Color(0xFF0056B3),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                otpSent -> {
                    Text(
                        text = "✅ OTP sent to 012-****789",
                        fontSize = 13.sp,
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = otp,
                onValueChange = { onOtpChange(it.filter { it.isDigit() }.take(6)) },
                label = { Text("OTP Code") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                enabled = otpSent && !isSending,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF0056B3),
                    unfocusedBorderColor = Color(0xFFDCE4EE),
                    disabledBorderColor = Color(0xFFDCE4EE)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (canResend) "Didn't receive the code?" else "Resend available in ${countdown}s",
                    fontSize = 12.sp,
                    color = if (canResend) Color(0xFF0056B3) else Color(0xFF8A9BB5)
                )

                if (canResend) {
                    Text(
                        text = "Resend OTP",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0056B3),
                        modifier = Modifier.clickable {
                            coroutineScope.launch {
                                isSending = true
                                otpSent = false
                                onSendOtp()
                                countdown = 60
                                canResend = false
                                onOtpChange("")
                                delay(1500)
                                isSending = false
                                otpSent = true
                            }
                        }
                    )
                } else {
                    Text(
                        text = "",
                        fontSize = 14.sp,
                        modifier = Modifier.height(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "💡 For demo, use OTP: 123456",
                fontSize = 11.sp,
                color = Color(0xFF8A9BB5)
            )
        }
    }
}