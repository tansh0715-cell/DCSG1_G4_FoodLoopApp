package com.example.assignment.ui.theme

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.SelectableChipColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val Purple40 = Color(0xFF6650a4)
val Pink40 = Color(0xFF7D5260)

val ContainerColor = Color(0xFFE5E5E5)

val PrimaryGreen = Color(0xFF138A56)
val SecondaryGreen = Color(0xFF01964B)

val PrimaryBlue = Color(0xFF2563EB)
val SecondaryBlue = Color(0xFFE2E8F0)
val BorderBlue = Color(0xFF1E293B)

val SecondaryYellow = Color(0xFFFFF8E1)
val BorderYellow = Color(0xFFF57C00)
val PrimaryYellow = Color(0xFFFFA000)
val BackgroundColor = Color(0xFFF8F9FA)
val SafeColor = Color(0xFFD9F8E9)
val textSoonColor = Color(0xfff39c12)
val SoonColor = Color(0xFFFBE8C1)
val textErrorColor = Color(0xFFEB5757)
val ErrorColor = Color(0xFFF6D4D3)
val PrimaryTextColor = Color(0xFF000000)

val SecondaryTextColor = Color(0xFF64748B)

val surfaceColor = Color(0xD3F1FFE7)


@Composable
fun navigationItemColors(): NavigationBarItemColors {
    return NavigationBarItemDefaults.colors(selectedIconColor = PrimaryGreen,
        selectedTextColor = PrimaryGreen,
        indicatorColor = surfaceColor,
        unselectedIconColor = SecondaryTextColor,
        unselectedTextColor = SecondaryTextColor)
}

@Composable
fun filterColors(): SelectableChipColors {
    return FilterChipDefaults.filterChipColors(selectedContainerColor = SecondaryGreen,
        selectedLabelColor = Color.White,
        containerColor = ContainerColor,
        labelColor = SecondaryTextColor,)
}

@Composable
fun appButtonColors(): ButtonColors {
    return ButtonDefaults.buttonColors(containerColor = SecondaryGreen, contentColor = Color.White)
}
