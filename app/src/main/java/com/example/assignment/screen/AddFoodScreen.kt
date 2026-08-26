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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.assignment.R
import com.example.assignment.components.FormField
import com.example.assignment.state.AddFoodEvent
import com.example.assignment.viewmodel.AddFoodViewModel
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFoodScreen(
    navController: NavController,
    innerPadding: PaddingValues,
    viewModel: AddFoodViewModel,
    onNavigateBack: () -> Unit = {navController.popBackStack()}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var selectedStartTime by remember { mutableStateOf("") }
    var selectedEndTime by remember { mutableStateOf("") }

    LaunchedEffect(uiState.pickupTime) {
        val parts = uiState.pickupTime.split(" - ")
        if (parts.size == 2) {
            selectedStartTime = parts[0].trim()
            selectedEndTime = parts[1].trim()
        } else if (uiState.pickupTime.isBlank()) {
            selectedStartTime = ""
            selectedEndTime = ""
        }
    }

    var categoryExpanded by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when(event){
                is AddFoodEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                AddFoodEvent.NavigateBack -> onNavigateBack()
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
            text = if (viewModel.editingFoodId == null) "Add Surplus Food" else "Edit Surplus Food",
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
                        .border(1.dp, MaterialTheme.colorScheme.onTertiary, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
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
            uiState.categoryError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        FormField(
            label = "Quantity Available",
            value = uiState.quantity,
            onValueChange = viewModel::onQtyChange,
            placeholder = "e.g. 5",
            errorMessage = uiState.qtyError,
            keyboardType = KeyboardType.Number
        )

        FormField(
            label = "Description (Optional)",
            value = uiState.description,
            onValueChange = viewModel::onDescriptionChange,
            placeholder = "Optional",
            singleLine = false,
            minLines = 3
        )

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
                OutlinedTextField(
                    value = selectedStartTime,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .weight(1f)
                        .clickable{
                            showStartTimePicker = true
                        },
                    placeholder = {
                        Text("Start Time")
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.onTertiary,
                        unfocusedContainerColor = MaterialTheme.colorScheme.background
                    ),
                    trailingIcon = {
                        Icon(painterResource(
                            R.drawable.alarm_24dp_2854c5_fill0_wght400_grad0_opsz24),
                            contentDescription = "Select start time",
                            tint = Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
                Text(
                    text = "to",
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontSize = 14.sp
                )
                OutlinedTextField(
                    value = selectedEndTime,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .weight(1f)
                        .clickable{
                            showEndTimePicker = true
                        },
                    placeholder = {
                        Text("End time")
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.onTertiary,
                        unfocusedContainerColor = Color.White
                    ),
                    trailingIcon = {
                        Icon(painterResource(
                            R.drawable.alarm_24dp_2854c5_fill0_wght400_grad0_opsz24),
                            contentDescription = "Time",
                            tint = Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
            }
            uiState.pickupTimeError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

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
            placeholder = "RM 0.00",
            errorMessage = uiState.priceError,
            keyboardType = KeyboardType.Decimal
        )

        FormField(
            label = "Discount Percentage (%)",
            value = uiState.selectedDiscount,
            onValueChange = viewModel::onDiscountChange,
            placeholder = "e.g. 50",
            keyboardType = KeyboardType.Number
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
            Text(
                text = if (viewModel.editingFoodId == null) "Publish Food" else "Update Food",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.background
            )
        }

        //confirm dialog
        if (uiState.showConfirmDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.dismissDialog() },
                title = {
                    Text(
                        if (viewModel.editingFoodId == null) "Confirm Publish"
                        else "Confirm Update"
                    )
                },
                text = {
                    Text(
                        if (viewModel.editingFoodId == null)
                            "Are you sure you want to publish this surplus food?"
                        else
                            "Are you sure you want to update this food listing?"
                    )
                },
                confirmButton = {
                    Button(onClick = { viewModel.confirmPublish() }) {
                        Text("Confirm", color = MaterialTheme.colorScheme.background)
                    }
                },
                dismissButton = {
                    Button(onClick = { viewModel.dismissDialog() }) {
                        Text("Cancel", color = MaterialTheme.colorScheme.background)
                    }
                }
            )
        }

        //start time picker
        if (showStartTimePicker) {
            val initial = parsePickerTime(selectedStartTime)
            val timePickerState = rememberTimePickerState(
                initialHour = initial?.first ?: LocalTime.now().hour,
                initialMinute = initial?.second ?: LocalTime.now().minute,
                is24Hour = false
            )

            AlertDialog(
                onDismissRequest = { showStartTimePicker = false },
                title = { Text("Select Start Time") },
                text = { TimePicker(state = timePickerState) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            selectedStartTime = formatPickerTime(
                                timePickerState.hour,
                                timePickerState.minute
                            )
                            showStartTimePicker = false

                            if (selectedEndTime.isNotBlank()) {
                                viewModel.onPickupTimeChange(
                                    "$selectedStartTime - $selectedEndTime"
                                )
                            }
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showStartTimePicker = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (showEndTimePicker) {
            val initial = parsePickerTime(selectedEndTime)
            val timePickerState = rememberTimePickerState(
                initialHour = initial?.first ?: LocalTime.now().hour,
                initialMinute = initial?.second ?: LocalTime.now().minute,
                is24Hour = false
            )

            AlertDialog(
                onDismissRequest = { showEndTimePicker = false },
                title = { Text("Select End Time") },
                text = { TimePicker(state = timePickerState) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            selectedEndTime = formatPickerTime(
                                timePickerState.hour,
                                timePickerState.minute
                            )
                            showEndTimePicker = false

                            if (selectedStartTime.isNotBlank()) {
                                viewModel.onPickupTimeChange(
                                    "$selectedStartTime - $selectedEndTime"
                                )
                            }
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEndTimePicker = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

private fun formatPickerTime(hour: Int, minute: Int): String {
    val amPm = if (hour < 12) "AM" else "PM"
    val displayHour = if (hour % 12 == 0) 12 else hour % 12

    return String.format(
        Locale.ENGLISH,
        "%02d:%02d %s",
        displayHour,
        minute,
        amPm
    )
}

private fun parsePickerTime(value: String): Pair<Int, Int>? {
    if (value.isBlank()) return null

    return try {
        val formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH)
        val time = LocalTime.parse(value.trim(), formatter)
        time.hour to time.minute
    } catch (_: Exception) {
        null
    }
}