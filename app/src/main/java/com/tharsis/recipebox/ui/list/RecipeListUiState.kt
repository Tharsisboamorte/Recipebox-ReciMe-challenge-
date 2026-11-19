package com.tharsis.recipebox.ui.list

import com.tharsis.recipebox.data.model.Recipe

/**
 * Immutable snapshot of everything the recipe list screen needs to render.
 */
data class RecipeListUiState(
    val isLoading: Boolean = true,
    val recipes: List<Recipe> = emptyList(),
    val errorMessage: String? = null,
    val searchText: String = "",
    val filters: RecipeFilters = RecipeFilters(),
) {
    val hasQuery: Boolean get() = searchText.isNotBlank() || filters.hasAny

    /** True when a successful load returned no matches (as opposed to loading). */
    val isEmptyResult: Boolean
        get() = !isLoading && errorMessage == null && recipes.isEmpty()
}
