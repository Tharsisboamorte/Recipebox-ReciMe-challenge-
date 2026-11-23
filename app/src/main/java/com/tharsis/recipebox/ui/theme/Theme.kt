package com.tharsis.recipebox.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

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
    outlineVariant = CardOutline,
)

/** Brand green/white theme always light, regardless of system setting. */
@Composable
fun RecipeBoxTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content,
    )
}
