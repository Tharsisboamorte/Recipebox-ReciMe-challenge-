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
    onPrimary = TextPrimary,
    primaryContainer = BrandGreenDark,
    onPrimaryContainer = TextOnGreen,
    secondary = BrandGreen,
    onSecondary = TextOnGreen,
    background = Color(0xFF101412),
    onBackground = TextOnGreen,
    surface = Color(0xFF161B19),
    onSurface = TextOnGreen,
    surfaceVariant = Color(0xFF24302C),
    onSurfaceVariant = BrandGreenSoft,
    outline = BrandGreenSoft,
)

@Composable
fun RecipeBoxTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    // Brand-first: keep the green/white identity even when the system is dark.
    //TODO: MAYBE ADD A DARK MODE WITH GREEN AND BLACK
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
