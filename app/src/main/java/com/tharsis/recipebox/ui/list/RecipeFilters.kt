package com.tharsis.recipebox.ui.list

import com.tharsis.recipebox.domain.model.RecipeSearchQuery

/**
 * UI-facing representation of the active filters. Mirrors [RecipeSearchQuery]
 * minus the free-text field (which the search bar owns) and exposes helpers for
 * rendering filter state.
 */
data class RecipeFilters(
    val vegetarianOnly: Boolean = false,
    val minServings: Int? = null,
    val maxServings: Int? = null,
    val includeIngredients: List<String> = emptyList(),
    val excludeIngredients: List<String> = emptyList(),
) {
    /** Number of distinct active filters, used for the filter badge. */
    val activeCount: Int
        get() = listOf(
            vegetarianOnly,
            minServings != null,
            maxServings != null,
            includeIngredients.isNotEmpty(),
            excludeIngredients.isNotEmpty(),
        ).count { it }

    val hasAny: Boolean get() = activeCount > 0
}

/** Combines the search text with [RecipeFilters] into a domain search query. */
fun RecipeFilters.toQuery(text: String): RecipeSearchQuery = RecipeSearchQuery(
    text = text.takeIf { it.isNotBlank() },
    vegetarianOnly = vegetarianOnly,
    minServings = minServings,
    maxServings = maxServings,
    includeIngredients = includeIngredients,
    excludeIngredients = excludeIngredients,
)
