package com.tharsis.recipebox.ui.list

/**
 * Every user/system event the list screen can emit.
 * The ViewModel is the only place that reacts to these intents.
 */
sealed interface RecipeListIntent {
    data class SearchChanged(val text: String) : RecipeListIntent
    data object ClearSearch : RecipeListIntent
    data class FiltersChanged(val filters: RecipeFilters) : RecipeListIntent
    data object ClearFilters : RecipeListIntent
    data object ToggleLayout : RecipeListIntent
    data object Retry : RecipeListIntent
    data class RecipeClicked(val recipeId: String) : RecipeListIntent
}

sealed interface RecipeListSideEffect {
    data class OpenDetail(val recipeId: String) : RecipeListSideEffect
}
