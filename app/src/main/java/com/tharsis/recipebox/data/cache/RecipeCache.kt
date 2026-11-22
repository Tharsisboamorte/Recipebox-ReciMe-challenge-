package com.tharsis.recipebox.data.cache

import com.tharsis.recipebox.data.model.Recipe

/** Offline snapshot of the recipe catalog. */
interface RecipeCache {
    suspend fun save(recipes: List<Recipe>)
    suspend fun load(): List<Recipe>?
}
