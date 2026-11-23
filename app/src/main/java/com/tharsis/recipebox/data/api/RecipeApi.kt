package com.tharsis.recipebox.data.api

import com.tharsis.recipebox.domain.model.Recipe
import com.tharsis.recipebox.domain.model.RecipeSearchQuery

/**
 * Abstraction over the recipe "backend". Today [LocalRecipeApi] reads bundled
 * JSON; shaped as a suspendable API so a real network client can replace it.
 */
interface RecipeApi {
    suspend fun getRecipes(): List<Recipe>
    suspend fun searchRecipes(query: RecipeSearchQuery): List<Recipe>
    suspend fun getRecipeById(id: String): Recipe?
}
