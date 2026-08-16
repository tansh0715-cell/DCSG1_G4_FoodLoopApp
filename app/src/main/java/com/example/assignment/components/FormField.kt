package com.example.assignment.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    minLines: Int = 1,
    errorMessage: String ?= null,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    val isError = errorMessage != null
    val isValid = errorMessage == null && value.isNotBlank()

    val defaultBroader = Color(0xFFE2E8F0)
    val focusedBorder = MaterialTheme.colorScheme.primary
    val errorBorder = MaterialTheme.colorScheme.error
    val successBorder = MaterialTheme.colorScheme.secondary

    val currentUnfocusedBorder = when{
        isError -> errorBorder
        isValid -> successBorder
        else -> defaultBroader
    }

    val currentFocusedBorder = when{
        isError -> errorBorder
        isValid -> successBorder
        else -> focusedBorder
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(text = placeholder, color = MaterialTheme.colorScheme.onSecondary, style = MaterialTheme.typography.bodyLarge) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            isError = isError,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = currentUnfocusedBorder,
                focusedBorderColor = currentFocusedBorder,
                errorBorderColor = errorBorder,

                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                errorContainerColor = Color.White
            ),
            singleLine = singleLine,
            minLines = minLines,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
        )

        if (isError) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }
    }
}