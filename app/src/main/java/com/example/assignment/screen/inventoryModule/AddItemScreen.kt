package com.example.assignment.screen.inventoryModule

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.assignment.R
import com.example.assignment.components.AppTopBar
import com.example.assignment.components.FormField
import com.example.assignment.model.inventoryModule.Food
import com.example.assignment.model.inventoryModule.FoodInput
import com.example.assignment.ui.theme.appButtonColors
import com.example.assignment.viewmodel.inventory.InventoryViewModel
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDate
import java.io.File
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.TextButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.Instant
import kotlinx.datetime.toLocalDate

@Composable
fun AddItemScreen(
    navController: NavController,
    vm: InventoryViewModel,
    innerPadding: PaddingValues){

    val context = LocalContext.current
    var foodName by remember { mutableStateOf("") }
    var foodNameError by remember { mutableStateOf<String?>(null) }
    var reminderDays by remember { mutableStateOf(1) }
    val reminderOptions = listOf(1, 2, 3, 5, 7)
    var categoryExpanded by remember { mutableStateOf(false) }
    var expireDate by remember { mutableStateOf("")}
    var showDatePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val todayStartLocal = java.time.LocalDate.now(java.time.ZoneId.systemDefault())
                    .atStartOfDay(java.time.ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()
                return utcTimeMillis >= todayStartLocal
            }
            override fun isSelectableYear(year: Int): Boolean {
                return year >= java.time.LocalDate.now(java.time.ZoneId.systemDefault()).year
            }
        }
    )

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            imageUri = capturedImageUri
        }
    }

    fun createImageUri(): Uri {
        val file = File.createTempFile(
            "camera_image_",
            ".jpg",
            context.externalCacheDir
        )

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }
    Column(modifier = Modifier.fillMaxSize()
        .padding(horizontal = 20.dp, vertical = 40.dp)
        .padding(innerPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp))
    {


        // Photo Preview and Selection
        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray)
                .clickable { galleryLauncher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Selected image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.add_photo_alternate_24dp_e3e3e3_fill0_wght400_grad0_opsz24),
                    contentDescription = "Select photo",
                    modifier = Modifier.size(48.dp),
                    tint = Color.Gray
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { galleryLauncher.launch("image/*") },
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Icon(painter = painterResource(R.drawable.add_photo_alternate_24dp_e3e3e3_fill0_wght400_grad0_opsz24), contentDescription = null
                ,tint = Color.White)
                Spacer(Modifier.size(4.dp))
                Text("Gallery",
                    color = MaterialTheme.colorScheme.background)
            }
            Button(
                onClick = {
                    if (hasCameraPermission) {
                        val uri = createImageUri()
                        capturedImageUri = uri
                        cameraLauncher.launch(uri)
                    } else {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }
            ) {
                Icon(painter = painterResource(R.drawable.add_a_photo_24dp_e3e3e3_fill0_wght400_grad0_opsz24), contentDescription = null
                , tint = Color.White)
                Spacer(Modifier.size(4.dp))
                Text("Camera",
                    color = MaterialTheme.colorScheme.background)
            }
        }

        FormField(
            label = "Name",
            value = foodName,
            onValueChange = {
                if (it.length <= 30) {
                    foodName = it
                    foodNameError = null
                } else {
                    foodNameError = "Food name must be 30 characters or less"
                }
            },
            placeholder = "Enter food name",
            maxLength = 30,
            errorMessage = foodNameError
        )

        // Category Dropdown
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Remind me before expiration",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = "$reminderDays days before",
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { categoryExpanded = true }
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledBorderColor = Color.Transparent,
                        disabledTextColor = if (reminderDays == 1) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onPrimary,
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
                    modifier = Modifier.background(Color.White)
                ) {
                    reminderOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text("$option days before") },
                            onClick = {
                                reminderDays = option
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }
        }
        Column(modifier = Modifier.fillMaxWidth()) {

            Text(
                text = "Expiry Date",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = expireDate,
                onValueChange = {},
                readOnly = true,
                enabled = false,
                placeholder = {
                    Text("Select expiry date")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        showDatePicker = true
                    },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = Color(0xFFE2E8F0),
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledPlaceholderColor = Color.Gray,
                    disabledContainerColor = MaterialTheme.colorScheme.background
                ),
                trailingIcon = {
                    Text(
                        text = "📅",
                        modifier = Modifier.padding(end = 12.dp)
                    )
                }
            )
        }

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = {
                    showDatePicker = false
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->

                                val date = Instant
                                    .fromEpochMilliseconds(millis)
                                    .toLocalDateTime(TimeZone.UTC)
                                    .date

                                expireDate = date.toString()
                            }

                            showDatePicker = false
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDatePicker = false
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(
                    state = datePickerState
                )
            }
        }

        Button(onClick = {
            if (foodName.isBlank()) {
                foodNameError = "Food name is required"
                return@Button
            }
            if (foodName.length > 30) {
                foodNameError = "Food name must be 30 characters or less"
                return@Button
            }
            val imageBytes = imageUri?.let { uri ->
                try {
                    context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                } catch (e: Exception) {
                    null
                }
            }
            vm.addItem(
                context = context,
                foodName = foodName.trim(),
                reminder_days = reminderDays,
                expireDate = expireDate,
                imageBytes = imageBytes,
                onSuccess = {
                    navController.popBackStack()
                }
            )
        },
            colors = appButtonColors(), shape = RoundedCornerShape(size = 20.dp)){
            Icon(painter = painterResource(R.drawable.add_24dp_e3e3e3_fill0_wght200_grad0_opsz24), contentDescription = "add")
            Text(text = "Add", style = MaterialTheme.typography.labelLarge)
        }

    }

}