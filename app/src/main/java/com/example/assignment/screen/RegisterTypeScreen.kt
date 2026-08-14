package com.example.assignment.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment.R

@Composable
fun RegisterTypeScreen(
    onBackClick: () -> Unit,
    onSelectSaver: () -> Unit,
    onSelectProvider: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = onBackClick) {
                Icon(painterResource(id = android.R.drawable.ic_menu_revert), contentDescription = "Back")
            }
            Text("Choose Account Type", fontSize = 19.sp, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.width(48.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.fillMaxWidth().weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text("Select the account type that fits your needs", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), textAlign = TextAlign.Center)

            Card(
                modifier = Modifier.fillMaxWidth().height(120.dp).border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                onClick = onSelectSaver
            ) {
                Row(modifier = Modifier.fillMaxSize().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Image(painterResource(id = R.drawable.foodsaver), contentDescription = "Food Saver", modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Food Saver", style = MaterialTheme.typography.titleMedium)
                        Text("Browse, reserve & manage food", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth().height(120.dp).border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                onClick = onSelectProvider
            ) {
                Row(modifier = Modifier.fillMaxSize().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Image(painterResource(id = R.drawable.foodprovider), contentDescription = "Food Provider", modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Food Provider", style = MaterialTheme.typography.titleMedium)
                        Text("Publish & manage food listings", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}