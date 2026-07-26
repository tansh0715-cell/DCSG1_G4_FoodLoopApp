package com.example.assignment.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryGreen,
    secondary = SecondaryGreen,
    background = BackgroundColor,
    onPrimary = PrimaryTextColor,
    onSecondary = SecondaryTextColor,
    surface = surfaceColor,
    error = textErrorColor

)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    secondary = SecondaryGreen,
    background = BackgroundColor,
    surface = surfaceColor,

    onPrimary = PrimaryTextColor,
    onSecondary = SecondaryTextColor,
    onTertiary = SecondaryBlue,

    primaryContainer = SafeColor,
    secondaryContainer = PrimaryBlue,

    tertiary = PrimaryYellow,
    tertiaryContainer = SoonColor,
    onTertiaryContainer = textSoonColor,
    outline = BorderYellow,
    surfaceVariant = SecondaryYellow,

    error = textErrorColor,
    errorContainer = ErrorColor

)

@Composable
fun AssignmentTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}