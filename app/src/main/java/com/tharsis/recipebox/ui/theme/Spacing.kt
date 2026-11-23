package com.tharsis.recipebox.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Shared spacing / shape tokens. Prefer these over one-off magic `.dp` values
 * so list, detail, and sheets stay visually consistent as the UI grows.
 */
object Spacing {
    val xxs: Dp = 4.dp
    val xs: Dp = 6.dp
    val sm: Dp = 8.dp
    val md: Dp = 12.dp
    val lg: Dp = 16.dp
    val xl: Dp = 20.dp
    val xxl: Dp = 24.dp
    val xxxl: Dp = 32.dp

    val cardRadius: Dp = 14.dp
    val imageRadius: Dp = 16.dp
    val chipRadius: Dp = 12.dp
    val buttonHeight: Dp = 44.dp
    val filterButtonHeight: Dp = 52.dp
    val iconButton: Dp = 48.dp
    val emptyIcon: Dp = 56.dp
}
