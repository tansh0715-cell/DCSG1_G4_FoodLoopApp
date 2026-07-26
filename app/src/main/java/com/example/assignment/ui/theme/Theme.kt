package com.example.assignment.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryGreen,
    secondary = SecondaryGreen,

    background = DarkBackground,
    surface = DarkSurface,

    onPrimary = Color.White,
    onBackground = DarkPrimaryText,
    onSurface = DarkPrimaryText,
    onSecondary = DarkSecondaryText,

    outline = DarkBorder,
    error = textErrorColor,


    primaryContainer = Color(0xFF064E3B),
    tertiary = PrimaryYellow,
    surfaceVariant = Color(0xFF422006),
    errorContainer = Color(0xFF450A0A)
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    secondary = SecondaryGreen,
    background = BackgroundColor,
    surface = Color.White,

    onPrimary = PrimaryTextColor,
    onSecondary = SecondaryTextColor,
    onTertiary = SecondaryBlue,

    primaryContainer = SafeColor,
    secondaryContainer = PrimaryBlue,

    tertiary = PrimaryYellow,
    tertiaryContainer = SoonColor,
    onTertiaryContainer = textSoonColor,

    outline = Color(0xFFE2E8F0),

    surfaceVariant = SecondaryYellow,
    error = textErrorColor,
    errorContainer = Color(0xFFFFEBEE)
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