package com.tharsis.recipebox.domain.model

/**
 * Domain search/filter parameters. Optional fields mirror a real search API.
 *
 * @property text free-text matched against title, description and instructions.
 * @property vegetarianOnly when true, only vegetarian recipes are returned.
 * @property minServings inclusive lower bound on servings, if set.
 * @property maxServings inclusive upper bound on servings, if set.
 * @property includeIngredients recipe must contain ALL of these ingredients.
 * @property excludeIngredients recipe must contain NONE of these ingredients.
 */
data class RecipeSearchQuery(
    val text: String? = null,
    val vegetarianOnly: Boolean = false,
    val minServings: Int? = null,
    val maxServings: Int? = null,
    val includeIngredients: List<String> = emptyList(),
    val excludeIngredients: List<String> = emptyList(),
) {
    /** True when no filters are applied and the full collection should return. */
    val isEmpty: Boolean
        get() = text.isNullOrBlank() &&
            !vegetarianOnly &&
            minServings == null &&
            maxServings == null &&
            includeIngredients.isEmpty() &&
            excludeIngredients.isEmpty()
}
