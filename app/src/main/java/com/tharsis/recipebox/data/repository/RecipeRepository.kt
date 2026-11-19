package com.tharsis.recipebox.data.repository

import com.tharsis.recipebox.data.api.RecipeApi
import com.tharsis.recipebox.data.api.RecipeSearchQuery
import com.tharsis.recipebox.data.model.Recipe

/**
 * Single source of truth the presentation layer talks to. Keeps ViewModels
 * agnostic of where recipes come from; today it simply delegates to
 * [RecipeApi], but could later add caching, pagination or offline storage.
 */
interface RecipeRepository {
    suspend fun getRecipes(): List<Recipe>
    suspend fun searchRecipes(query: RecipeSearchQuery): List<Recipe>
    suspend fun getRecipeById(id: String): Recipe?
}

class RecipeRepositoryImpl(
    private val api: RecipeApi,
) : RecipeRepository {
    override suspend fun getRecipes(): List<Recipe> = api.getRecipes()

    override suspend fun searchRecipes(query: RecipeSearchQuery): List<Recipe> =
        api.searchRecipes(query)

    override suspend fun getRecipeById(id: String): Recipe? = api.getRecipeById(id)
}
