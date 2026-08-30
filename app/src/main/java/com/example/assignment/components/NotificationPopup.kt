package com.example.assignment.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NotificationPopup(
    title: String,
    message: String,
    visible: Boolean,
    onDismiss: () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { -it }
        ) + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { -it }
        ) + fadeOut()
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                ),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            )
        ) {

            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text = "🔔",
                    fontSize = 24.sp
                )

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = message,
                        fontSize = 13.sp
                    )
                }
                IconButton(
                    onClick = onDismiss
                ) {
                    Text("×")
                }
            }
        }
    }
}