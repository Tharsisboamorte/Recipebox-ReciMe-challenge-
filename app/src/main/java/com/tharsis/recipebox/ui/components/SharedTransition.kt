package com.tharsis.recipebox.ui.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Keys that tie a list item's visuals to their detail-screen counterparts so
 * Compose can run a shared-element transition between the two destinations.
 */
object RecipeSharedKeys {
    fun image(id: String) = "recipe-image-$id"
    fun title(id: String) = "recipe-title-$id"
}

/**
 * Returns a [Modifier] that participates in a shared-element transition when
 * both a [SharedTransitionScope] and [AnimatedVisibilityScope] are available,
 * and a no-op [Modifier] otherwise (e.g. in `@Preview`). This lets the same
 * composables be reused in previews without wiring up transition scopes.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun sharedElementModifier(
    sharedScope: SharedTransitionScope?,
    animatedScope: AnimatedVisibilityScope?,
    key: String,
): Modifier {
    if (sharedScope == null || animatedScope == null) return Modifier
    return with(sharedScope) {
        Modifier.sharedElement(
            rememberSharedContentState(key = key),
            animatedVisibilityScope = animatedScope,
        )
    }
}
