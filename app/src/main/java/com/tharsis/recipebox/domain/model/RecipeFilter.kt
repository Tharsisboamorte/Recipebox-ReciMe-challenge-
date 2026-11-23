package com.tharsis.recipebox.domain.model

/**
 * Pure Kotlin filtering for [RecipeSearchQuery].
 * Framework-free so search semantics can be unit-tested without Android or I/O.
 */
object RecipeFilter {

    fun apply(recipes: List<Recipe>, query: RecipeSearchQuery): List<Recipe> {
        if (query.isEmpty) return recipes

        val text = query.text?.trim()?.lowercase().orEmpty()
        val include = query.includeIngredients.map { it.trim().lowercase() }.filter { it.isNotEmpty() }
        val exclude = query.excludeIngredients.map { it.trim().lowercase() }.filter { it.isNotEmpty() }

        return recipes.filter { recipe ->
            recipe.matchesText(text) &&
                recipe.matchesVegetarian(query.vegetarianOnly) &&
                recipe.matchesServings(query.minServings, query.maxServings) &&
                recipe.matchesIncludedIngredients(include) &&
                recipe.matchesExcludedIngredients(exclude)
        }
    }

    private fun Recipe.matchesText(text: String): Boolean {
        if (text.isEmpty()) return true
        val haystack = buildString {
            append(title.lowercase())
            append(' ')
            append(description.lowercase())
            append(' ')
            append(instructions.joinToString(" ").lowercase())
        }
        return haystack.contains(text)
    }

    private fun Recipe.matchesVegetarian(vegetarianOnly: Boolean): Boolean =
        !vegetarianOnly || dietary.vegetarian

    private fun Recipe.matchesServings(min: Int?, max: Int?): Boolean =
        (min == null || servings >= min) && (max == null || servings <= max)

    private fun Recipe.matchesIncludedIngredients(include: List<String>): Boolean {
        if (include.isEmpty()) return true
        val lowered = ingredients.map { it.lowercase() }
        return include.all { needle -> lowered.any { it.contains(needle) } }
    }

    private fun Recipe.matchesExcludedIngredients(exclude: List<String>): Boolean {
        if (exclude.isEmpty()) return true
        val lowered = ingredients.map { it.lowercase() }
        return exclude.none { needle -> lowered.any { it.contains(needle) } }
    }
}
