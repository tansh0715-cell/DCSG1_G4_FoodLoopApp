package com.example.assignment.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.assignment.R
import com.example.assignment.components.FormField
import com.example.assignment.state.AddFoodEvent
import com.example.assignment.viewmodel.AddFoodViewModel

@Composable
fun AddFoodScreen(navController: NavController,innerPadding: PaddingValues, viewModel: AddFoodViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    var categoryExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when(event){
                is AddFoodEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is AddFoodEvent.NavigateBack -> {
                    navController.navigate("home"){
                        popUpTo("home") { inclusive = true }
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(innerPadding)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Add Surplus Food",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onPrimary
        )

        // Food Name
        FormField(
            label = "Food Name",
            value = uiState.foodName,
            onValueChange = viewModel::onNameChange,
            placeholder = "Enter food name"
        )

        if(uiState.nameError != null){
            Text(
                text = uiState.nameError!!,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp
            )
        }

        // Category Dropdown
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Category",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = uiState.selectedCategory,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { categoryExpanded = true }
                        .border(1.dp, MaterialTheme.colorScheme.onTertiary, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledBorderColor = Color.Transparent,
                        disabledTextColor = if (uiState.selectedCategory == "Select Category") MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.background
                    ),
                    trailingIcon = {
                        Text("▼", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSecondary, modifier = Modifier.padding(end = 12.dp))
                    }
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { categoryExpanded = true }
                )
                DropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.background)
                ) {
                    viewModel.categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                viewModel.onCategoryChange(cat)
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }
        }

        // Quantity Available
        FormField(
            label = "Quantity Available",
            value = uiState.quantity,
            onValueChange = viewModel::onQtyChange,
            placeholder = "e.g. 5"
        )

        // Description
        FormField(
            label = "Description (Optional)",
            value = uiState.description,
            onValueChange = viewModel::onDescriptionChange,
            placeholder = "Optional",
            singleLine = false,
            minLines = 3
        )

        // Pickup Time Range
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Pickup Time Range",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Start Time
                OutlinedTextField(
                    value = "06:00 PM",
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.onTertiary,
                        unfocusedContainerColor = MaterialTheme.colorScheme.background
                    ),
                    trailingIcon = {
                        Icon(painterResource(R.drawable.alarm_24dp_2854c5_fill0_wght400_grad0_opsz24), contentDescription = "Time", tint = Color.Gray, modifier = Modifier.size(18.dp))
                    }
                )
                Text(text = "to", color = MaterialTheme.colorScheme.onSecondary, fontSize = 14.sp)
                // End Time
                OutlinedTextField(
                    value = "08:00 PM",
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.onTertiary,
                        unfocusedContainerColor = Color.White
                    ),
                    trailingIcon = {
                        Icon(painterResource(R.drawable.alarm_24dp_2854c5_fill0_wght400_grad0_opsz24), contentDescription = "Time", tint = Color.Gray, modifier = Modifier.size(18.dp))
                    }
                )
            }
        }

        // doted line frame (food upload)
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Food Image",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            val stroke = Stroke(width = 3f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f))
            val primaryColor = MaterialTheme.colorScheme.primary
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .drawBehind() { drawRoundRect(color = primaryColor.copy(alpha = 0.4f), style = stroke, cornerRadius = CornerRadius(12.dp.toPx())) }
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                    .clickable { /* Handle Image Upload */ },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(R.drawable.add_circle_24dp_e3e3e3_fill0_wght400_grad0_opsz24),
                        contentDescription = "Upload",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Tap to upload image", color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
                }
            }
        }

        FormField(
            label = "Original Price (RM)",
            value = uiState.originalPrice,
            onValueChange = viewModel::onPriceChange,
            placeholder = "RM 0.00"
        )

        FormField(
            label = "Discount Percentage (%)",
            value = uiState.selectedDiscount,
            onValueChange = viewModel::onDiscountChange,
            placeholder = "e.g. 50"
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { viewModel.submitFood() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Publish Food", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.background)
        }

        //confirm dialog
        if (uiState.showConfirmDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.dismissDialog() },
                title = { Text("Confirm Publish") },
                text = { Text("Are you sure you want to publish this surplus food?") },
                confirmButton = {
                    Button(
                        onClick = { viewModel.confirmPublish()}
                    ) {
                        Text("Confirm", color = MaterialTheme.colorScheme.background
                        )
                    }
                },
                dismissButton = {
                    Button(onClick = { viewModel.dismissDialog() }) { Text("Cancel",color = MaterialTheme.colorScheme.background) }
                }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}