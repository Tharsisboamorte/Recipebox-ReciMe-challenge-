package com.tharsis.recipebox.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Precision
import coil3.size.Size

/**
 * Absolute pixel decode caps (NOT density-scaled). Density-based Dp targets
 * ballooned to 880×1120 on xxxhdpi emulators — larger than the source — so
 * Coil never downsampled and native heap stayed high while scrolling.
 */
enum class RecipeImageSize(val widthPx: Int, val heightPx: Int) {
    /** Grid / list thumbnails. */
    Thumbnail(360, 480),

    /** Detail hero banner. */
    Hero(900, 560),
}

/**
 * Recipe image loaded remotely via Coil with a hard-capped decode size.
 */
@Composable
fun RecipeImage(
    imageUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: RecipeImageSize = RecipeImageSize.Thumbnail,
) {
    val context = LocalContext.current
    val widthPx = size.widthPx
    val heightPx = size.heightPx

    val request = remember(imageUrl, widthPx, heightPx) {
        if (imageUrl.isNullOrBlank()) {
            null
        } else {
            ImageRequest.Builder(context)
                .data(imageUrl)
                .size(Size(widthPx, heightPx))
                .precision(Precision.INEXACT)
                .memoryCacheKey("$imageUrl-${widthPx}x$heightPx")
                .crossfade(120)
                .build()
        }
    }

    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        if (request == null) {
            Icon(
                imageVector = Icons.Outlined.RestaurantMenu,
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(40.dp),
            )
        } else {
            AsyncImage(
                model = request,
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
