package com.tharsis.recipebox.data.local

import com.tharsis.recipebox.domain.model.Recipe

/** Offline snapshot of the recipe catalog. */
interface RecipeCache {
    suspend fun save(recipes: List<Recipe>)
    suspend fun load(): List<Recipe>?
}
