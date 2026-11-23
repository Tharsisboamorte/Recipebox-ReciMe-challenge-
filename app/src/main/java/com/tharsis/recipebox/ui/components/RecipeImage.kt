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
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Precision
import coil3.size.Size
import com.tharsis.recipebox.core.config.AppConfig
import com.tharsis.recipebox.ui.theme.Spacing

/**
 * Absolute pixel decode caps (NOT density-scaled). Density-based Dp targets
 * ballooned on xxxhdpi — larger than the source — so Coil never downsampled
 * and native heap stayed high while scrolling.
 */
enum class RecipeImageSize(val widthPx: Int, val heightPx: Int) {
    Thumbnail(AppConfig.Image.THUMBNAIL_WIDTH_PX, AppConfig.Image.THUMBNAIL_HEIGHT_PX),
    Hero(AppConfig.Image.HERO_WIDTH_PX, AppConfig.Image.HERO_HEIGHT_PX),
}

/**
 * Recipe image loaded via Coil (bundled asset or remote URL) with a hard-capped
 * decode size. Asset paths work offline; remote URLs also hit Coil's disk cache.
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
            val cacheKey = "$imageUrl-${widthPx}x$heightPx"
            ImageRequest.Builder(context)
                .data(imageUrl)
                .size(Size(widthPx, heightPx))
                .precision(Precision.INEXACT)
                .memoryCacheKey(cacheKey)
                .diskCacheKey(cacheKey)
                .crossfade(AppConfig.Image.REQUEST_CROSSFADE_MS)
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
                modifier = Modifier.size(Spacing.xl * 2),
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
