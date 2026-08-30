package com.example.assignment.screen

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults.colors
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import coil3.compose.AsyncImage
import com.example.assignment.R
import com.example.assignment.components.FormField
import com.example.assignment.state.AddFoodEvent
import com.example.assignment.ui.theme.PrimaryGreen
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
    //observe viewmodel state
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri

        if (uri != null) {
            viewModel.onImageSelected(uri)
        }
    }

    //local ui states
    var selectedStartTime by remember { mutableStateOf("") }
    var selectedEndTime by remember { mutableStateOf("") }

    var showTimeInputDialog by remember { mutableStateOf(false) }
    var editingStartTime by remember { mutableStateOf(true) }

    var inputHour by remember { mutableStateOf("") }
    var inputMinute by remember { mutableStateOf("") }
    var inputAmPm by remember { mutableStateOf("AM") }
    var timeInputError by remember { mutableStateOf<String?>(null) }
    var pickupRangeError by remember {
        mutableStateOf(false)
    }

    var amPmExpanded by remember { mutableStateOf(false) }

    val pickupTimeValid =
        if (
            selectedStartTime.isNotBlank() &&
            selectedEndTime.isNotBlank()
        ) {
            runCatching {
                val formatter =
                    DateTimeFormatter.ofPattern(
                        "hh:mm a",
                        Locale.ENGLISH
                    )

                val start = LocalTime.parse(
                    selectedStartTime,
                    formatter
                )

                val end = LocalTime.parse(
                    selectedEndTime,
                    formatter
                )

                start.isBefore(end)

            }.getOrDefault(false)
        } else {
            true
        }

    //sync pickupTime from UiState
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

    //One-time event collector for success/error messages and navigation
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when(event){
                is AddFoodEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                AddFoodEvent.NavigateToProviderHome-> onNavigateBack()
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
                    colors = colors(
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

        // Quantity Available
        FormField(
            label = "Quantity Available",
            value = uiState.quantity,
            onValueChange = viewModel::onQtyChange,
            placeholder = "e.g. 5",
            errorMessage = uiState.qtyError,
            keyboardType = KeyboardType.Number
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
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
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
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            editingStartTime = true
                            timeInputError = null

                            val existing = parsePickerTime(selectedStartTime)

                            inputHour = existing?.first?.toString() ?: ""
                            inputMinute = existing?.second?.toString()?.padStart(2, '0') ?: ""
                            inputAmPm = if (
                                existing != null && existing.first >= 12
                            ) {
                                "PM"
                            } else {
                                "AM"
                            }

                            showTimeInputDialog = true
                        }
                ) {
                    OutlinedTextField(
                        value = selectedStartTime,
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("Start Time")
                        },
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            Icon(
                                painter = painterResource(
                                    R.drawable.alarm_24dp_2854c5_fill0_wght400_grad0_opsz24
                                ),
                                contentDescription = "Select start time",
                                tint = Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                }

                Text(
                    text = "to",
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontSize = 14.sp
                )

                // End Time
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            editingStartTime = false
                            timeInputError = null

                            val existing = parsePickerTime(selectedEndTime)

                            inputHour = existing?.first?.toString() ?: ""
                            inputMinute = existing?.second?.toString()?.padStart(2, '0') ?: ""
                            inputAmPm = if (
                                existing != null && existing.first >= 12
                            ) {
                                "PM"
                            } else {
                                "AM"
                            }

                            showTimeInputDialog = true
                        }
                ) {
                    OutlinedTextField(
                        value = selectedEndTime,
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("End Time")
                        },
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            Icon(
                                painter = painterResource(
                                    R.drawable.alarm_24dp_2854c5_fill0_wght400_grad0_opsz24
                                ),
                                contentDescription = "Select end time",
                                tint = Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                }
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

        if (pickupRangeError) {
            Text(
                text = "Pickup end time must be later than start time.",
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
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
                    .clickable {
                        imagePickerLauncher.launch("image/*")
                    },
                contentAlignment = Alignment.Center
            ) {
                when {

                    // Newly selected local image
                    uiState.imageUri != null -> {

                        AsyncImage(
                            model = uiState.imageUri,
                            contentDescription = "Selected food image",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Existing image from Supabase during edit
                    uiState.imageUrl != null -> {

                        AsyncImage(
                            model = uiState.imageUrl,
                            contentDescription = "Food image",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    else -> {

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Icon(
                                painter = painterResource(
                                    R.drawable.add_circle_24dp_e3e3e3_fill0_wght400_grad0_opsz24
                                ),
                                contentDescription = "Upload",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )

                            Spacer(
                                modifier = Modifier.height(8.dp)
                            )

                            Text(
                                text = "Tap to upload image",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 13.sp
                            )
                        }
                    }
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
            onClick = {
                if(pickupTimeValid){
                    pickupRangeError = false
                    viewModel.submitFood()
                }else{
                    pickupRangeError = true
                }
            },
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
                            "Are you sure you want to publish this surplus food? Once published, it cannot be edited or deleted until all portions are sold out."
                        else
                            "This food is sold out. Update the details and quantity to make it available again?"
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val imageBytes = uiState.imageUri?.let { uriString ->
                                try {
                                    context.contentResolver
                                        .openInputStream(
                                            Uri.parse(uriString)
                                        )
                                        ?.use { input ->
                                            input.readBytes()
                                        }
                                } catch (_: Exception) {
                                    null
                                }
                            }
                            viewModel.confirmPublish(
                                imageBytes = imageBytes
                            )
                        }
                    ) {
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
        if (showTimeInputDialog) {
            AlertDialog(
                onDismissRequest = {
                    showTimeInputDialog = false
                },
                title = {
                    Text(
                        if (editingStartTime)
                            "Enter Start Time"
                        else
                            "Enter End Time"
                    )
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {

                            OutlinedTextField(
                                value = inputHour,
                                onValueChange = {
                                    inputHour = it.filter { char ->
                                        char.isDigit()
                                    }.take(2)
                                    timeInputError = null
                                },
                                label = {
                                    Text("Hour")
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number
                                ),
                                colors = colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = Color.Gray,
                                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                                    unfocusedLabelColor = Color.Gray,
                                    cursorColor = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.weight(1f)
                            )

                            OutlinedTextField(
                                value = inputMinute,
                                onValueChange = {
                                    inputMinute = it.filter { char ->
                                        char.isDigit()
                                    }.take(2)
                                    timeInputError = null
                                },
                                label = {
                                    Text("Minute")
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number
                                ),
                                colors = colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = Color.Gray,
                                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                                    unfocusedLabelColor = Color.Gray,
                                    cursorColor = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.weight(1f)
                            )

                            Box(
                                modifier = Modifier.weight(1f)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        amPmExpanded = true
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.onSurface
                                    ),
                                    border = BorderStroke(
                                        1.dp,
                                        Color.Gray
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = inputAmPm,
                                        fontSize = 16.sp
                                    )
                                }

                                DropdownMenu(
                                    expanded = amPmExpanded,
                                    onDismissRequest = {
                                        amPmExpanded = false
                                    }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("AM") },
                                        onClick = {
                                            inputAmPm = "AM"
                                            amPmExpanded = false
                                        }
                                    )

                                    DropdownMenuItem(
                                        text = { Text("PM") },
                                        onClick = {
                                            inputAmPm = "PM"
                                            amPmExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        timeInputError?.let { error ->
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {

                            val hour = inputHour.toIntOrNull()
                            val minute = inputMinute.toIntOrNull()

                            if (hour == null || hour !in 1..12) {
                                timeInputError =
                                    "Hour must be between 1 and 12"
                                return@TextButton
                            }

                            if (minute == null || minute !in 0..59) {
                                timeInputError =
                                    "Minute must be between 0 and 59"
                                return@TextButton
                            }

                            var hour24 = hour

                            if (inputAmPm == "AM") {
                                if (hour == 12) {
                                    hour24 = 0
                                }
                            } else {
                                if (hour != 12) {
                                    hour24 = hour + 12
                                }
                            }

                            val formattedTime =
                                formatPickerTime(hour24, minute)

                            if (editingStartTime) {
                                selectedStartTime = formattedTime
                            } else {
                                selectedEndTime = formattedTime
                            }

                            val start =
                                if (editingStartTime)
                                    formattedTime
                                else
                                    selectedStartTime

                            val end =
                                if (editingStartTime)
                                    selectedEndTime
                                else
                                    formattedTime

                            if (
                                start.isNotBlank() &&
                                end.isNotBlank()
                            ) {
                                viewModel.onPickupTimeChange(
                                    "$start - $end"
                                )
                            }

                            showTimeInputDialog = false
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showTimeInputDialog = false
                        }
                    ) {
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