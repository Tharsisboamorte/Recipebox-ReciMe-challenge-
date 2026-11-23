package com.tharsis.recipebox.domain.repository

import com.tharsis.recipebox.domain.model.Recipe
import com.tharsis.recipebox.domain.model.RecipeSearchQuery

/**
 * Domain contract for recipe access. Implementations live in `data/`.
 * UI / ViewModels depend on this interface only — never on DTOs or assets.
 */
interface RecipeRepository {
    suspend fun getRecipes(): List<Recipe>
    suspend fun searchRecipes(query: RecipeSearchQuery): List<Recipe>
    suspend fun getRecipeById(id: String): Recipe?
}
