package com.tharsis.recipebox.data.api

import com.tharsis.recipebox.data.model.Recipe

/**
 * Abstraction over the recipe "backend". Today it is backed by a bundled JSON
 * file ([LocalRecipeApi]); it is shaped as a suspendable, query-driven API so a
 * real network implementation can be dropped in without touching callers.
 */
interface RecipeApi {
    /** Returns every recipe in the collection. */
    suspend fun getRecipes(): List<Recipe>

    /** Returns recipes matching the given [query]; an empty query returns all. */
    suspend fun searchRecipes(query: RecipeSearchQuery): List<Recipe>

    /** Returns a single recipe by [id], or null if it does not exist. */
    suspend fun getRecipeById(id: String): Recipe?
}

/**
 * Parameters for the search endpoint. Every field is optional, mirroring how a
 * real search API would accept optional query parameters.
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
