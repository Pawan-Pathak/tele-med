package com.telemed.demo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = BrandPrimary,
    onPrimary = Color(0xFF1A1A1A),
    primaryContainer = BrandPrimaryContainer,
    onPrimaryContainer = BrandPrimaryDark,
    secondary = AccentTeal,
    onSecondary = Color.White,
    secondaryContainer = AccentTealContainer,
    onSecondaryContainer = AccentTeal,
    tertiary = PharmacistColor,
    onTertiary = Color.White,
    background = BackgroundPage,
    onBackground = TextPrimary,
    surface = BackgroundCard,
    onSurface = TextPrimary,
    surfaceVariant = BackgroundPage,
    onSurfaceVariant = TextSecondary,
    error = StatusAlertText,
    onError = Color.White,
    outline = OutlineGray,
    outlineVariant = DividerColor
)

private val DarkColorScheme = darkColorScheme(
    primary = BrandPrimaryLight,
    onPrimary = Color(0xFF1A1A1A),
    primaryContainer = BrandPrimaryDark,
    onPrimaryContainer = BrandPrimaryContainer,
    secondary = AccentTealLight,
    onSecondary = Color.White,
    background = BackgroundDark,
    onBackground = Color.White,
    surface = SurfaceDark,
    onSurface = Color.White,
    error = Color(0xFFEF9A9A),
    onError = Color(0xFFB71C1C)
)

private val AppShapes = Shapes(
    small = RoundedCornerShape(10.dp),     // Form fields
    medium = RoundedCornerShape(16.dp),    // Cards
    large = RoundedCornerShape(24.dp),     // Buttons (pill), badges
    extraLarge = RoundedCornerShape(20.dp) // Pill badges
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
        shapes = AppShapes,
        content = content
    )
}
