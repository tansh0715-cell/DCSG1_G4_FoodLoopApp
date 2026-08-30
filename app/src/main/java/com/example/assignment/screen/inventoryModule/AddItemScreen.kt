package com.example.assignment.screen.inventoryModule

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment.R
import com.example.assignment.components.FormField
import com.example.assignment.ui.theme.appButtonColors

@Composable
fun AddItemScreen(onBack: ()-> Unit){
    var foodName by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("")}
    var selectedCategory by remember { mutableStateOf("Select Category") }
    var categoryExpanded by remember { mutableStateOf(false) }
    val categories = listOf("g", "ml", "None")
    var daysUntilExpiry by remember { mutableStateOf("")}

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp))
    {
        Text(text = "Add Item", style = MaterialTheme.typography.headlineLarge)
        FormField(label = "Name", value = foodName, onValueChange = {foodName = it}, placeholder = "Enter food name")
        FormField(label = "Quantity", value = quantity, onValueChange = {quantity = it}, placeholder = "Enter the quantity")

        // Category Dropdown
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Unit",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { categoryExpanded = true }
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp)), // 🔥 显式添加边框
                    shape = RoundedCornerShape(12.dp),
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledBorderColor = Color.Transparent,
                        disabledTextColor = if (selectedCategory == "Select Category") MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onPrimary,
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
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                selectedCategory = cat
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }
        }
        FormField(label = "Days until expiry", value = daysUntilExpiry, onValueChange = {daysUntilExpiry=it}, placeholder = "Enter numbers")
        Button(onClick = { onBack() }, colors = appButtonColors(), shape = RoundedCornerShape(size = 20.dp)){
            Icon(painter = painterResource(R.drawable.add_24dp_e3e3e3_fill0_wght200_grad0_opsz24), contentDescription = "add")
            Text(text = "Add", style = MaterialTheme.typography.labelLarge)
        }

    }

}