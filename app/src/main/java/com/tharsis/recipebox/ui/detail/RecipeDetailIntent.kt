package com.tharsis.recipebox.ui.detail

/** User/system events for the recipe detail screen. */
sealed interface RecipeDetailIntent {
    data object Load : RecipeDetailIntent
    data object Retry : RecipeDetailIntent
    data object Back : RecipeDetailIntent
}

/** One-shot effects consumed by the Route (navigation, etc.). */
sealed interface RecipeDetailSideEffect {
    data object NavigateBack : RecipeDetailSideEffect
}
