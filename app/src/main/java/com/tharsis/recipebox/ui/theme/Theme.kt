package com.tharsis.recipebox.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = BrandGreen,
    onPrimary = TextOnGreen,
    primaryContainer = BrandGreenContainer,
    onPrimaryContainer = BrandGreenDark,
    secondary = BrandGreenSoft,
    onSecondary = TextOnGreen,
    secondaryContainer = BrandGreenContainer,
    onSecondaryContainer = BrandGreenDark,
    tertiary = BrandGreenDark,
    onTertiary = TextOnGreen,
    background = AppBackground,
    onBackground = TextPrimary,
    surface = AppSurface,
    onSurface = TextPrimary,
    surfaceVariant = CardSurface,
    onSurfaceVariant = TextSecondary,
    outline = BrandGreenSoft,
    outlineVariant = CardSurface,
)

private val DarkColorScheme = darkColorScheme(
    primary = BrandGreenSoft,
    onPrimary = Color(0xFF06261D),
    primaryContainer = BrandGreenDark,
    onPrimaryContainer = Color(0xFFE8F5F1),
    secondary = BrandGreen,
    onSecondary = TextOnGreen,
    secondaryContainer = Color(0xFF1E3A32),
    onSecondaryContainer = BrandGreenSoft,
    tertiary = BrandGreenSoft,
    onTertiary = Color(0xFF06261D),
    background = Color(0xFF101412),
    onBackground = Color(0xFFF2F4F3),
    surface = Color(0xFF161B19),
    onSurface = Color(0xFFF2F4F3),
    surfaceVariant = Color(0xFF24302C),
    onSurfaceVariant = Color(0xFFB8C5C0),
    outline = BrandGreenSoft,
    outlineVariant = Color(0xFF2F3D38),
)

@Composable
fun RecipeBoxTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content,
    )
}
