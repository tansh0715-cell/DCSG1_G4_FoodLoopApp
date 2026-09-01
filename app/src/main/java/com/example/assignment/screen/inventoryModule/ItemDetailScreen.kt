package com.example.assignment.screen.inventoryModule

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.assignment.R
import com.example.assignment.viewmodel.inventory.InventoryViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext

@Composable
fun ItemDetailScreen(
    innerPadding: PaddingValues,
    vm: InventoryViewModel,
    itemId: String?,
    navController: NavController) {


    var showDeleteDialog by remember { mutableStateOf(false) }
    val food = vm.foods.find { it.item_id == itemId }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        vm.loadInventory(context)
    }

    if (food == null) {
        Text(
            text = "Food item not found",
            modifier = Modifier.padding(innerPadding)
        )
        return
    }

    var reminderDays by remember(food.item_id) {
        mutableStateOf(food.reminder_days)
    }

    var reminderExpanded by remember {
        mutableStateOf(false)
    }

    if (food == null) {
        Text("Food not found")
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(24.dp)
    ) {
        // Food Name
        Text(
            text = food.name,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Expiry Date
        Text(
            text = "Expiry Date",
            style = MaterialTheme.typography.labelLarge
        )

        Text(
            text = food.expireDate.toString(),
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Reminder Days
        Text(
            text = "Reminder Days",
            style = MaterialTheme.typography.labelLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {

            OutlinedTextField(
                value = "$reminderDays days before",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        reminderExpanded = true
                    },
                shape = RoundedCornerShape(12.dp),
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor =
                        MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor =
                        MaterialTheme.colorScheme.outline,
                    disabledContainerColor =
                        MaterialTheme.colorScheme.background
                ),
                trailingIcon = {
                    Text(
                        text = "▼",
                        fontSize = 10.sp,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                }
            )

            // Make the whole TextField clickable
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable {
                        reminderExpanded = true
                    }
            )

            DropdownMenu(
                expanded = reminderExpanded,
                onDismissRequest = {
                    reminderExpanded = false
                }
            ) {

                listOf(1, 2, 3, 5, 7).forEach { option ->

                    DropdownMenuItem(
                        text = {
                            Text("$option days before")
                        },
                        onClick = {

                            reminderDays = option
                            reminderExpanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        ElevatedButton(
            onClick = {

                vm.updateReminder(
                    context = context,
                    food = food,
                    newReminderDays = reminderDays,
                    onSuccess = {
                        navController.popBackStack()
                    }
                )

            },
            shape = RoundedCornerShape(20.dp)
        ) {
            Text("Save Reminder")
        }

        Spacer(modifier = Modifier.height(16.dp))
        ElevatedButton(onClick = {
            showDeleteDialog = true
        },
            shape = RoundedCornerShape(size = 20.dp),
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            )
        ){
            Icon(painter = painterResource(R.drawable.delete_24dp_e3e3e3_fill0_wght400_grad0_opsz24_1_), contentDescription = "add")
            Text(text = "Delete Item", style = MaterialTheme.typography.labelLarge) }
        }
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
            },
            title = {
                Text("Delete Item?")
            },
            text = {
                Text("Are you sure you want to delete ${food.name}?")
            },
            confirmButton = {
                TextButton(
                    onClick = {

                        vm.deleteItem(
                            context = context,
                            itemId = food.item_id,
                            onSuccess = {
                                navController.popBackStack()
                            }
                        )
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

