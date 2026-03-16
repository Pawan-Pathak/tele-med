package com.telemed.demo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PrimaryTeal,
    onPrimary = Color.White,
    primaryContainer = PrimaryTealVeryLight,
    onPrimaryContainer = PrimaryTealDark,
    secondary = SecondaryGreen,
    onSecondary = Color.White,
    secondaryContainer = SecondaryGreenLight,
    onSecondaryContainer = SecondaryGreenDark,
    tertiary = AccentBlue,
    onTertiary = Color.White,
    background = BackgroundWarm,
    onBackground = TextPrimary,
    surface = SurfaceWarm,
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFFF5F0E8),
    onSurfaceVariant = TextSecondary,
    error = AccentRed,
    onError = Color.White,
    outline = DividerColor
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryTealLight,
    onPrimary = PrimaryTealDark,
    primaryContainer = PrimaryTeal,
    onPrimaryContainer = PrimaryTealVeryLight,
    secondary = SecondaryGreenLight,
    onSecondary = SecondaryGreenDark,
    background = BackgroundWarmDark,
    onBackground = Color.White,
    surface = SurfaceDark,
    onSurface = Color.White,
    error = Color(0xFFEF9A9A),
    onError = Color(0xFFB71C1C)
)

@Composable
fun TeleMedTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
