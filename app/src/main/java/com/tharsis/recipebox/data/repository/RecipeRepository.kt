package com.tharsis.recipebox.data.repository

import com.tharsis.recipebox.data.api.RecipeApi
import com.tharsis.recipebox.data.api.RecipeFilter
import com.tharsis.recipebox.data.api.RecipeSearchQuery
import com.tharsis.recipebox.data.cache.RecipeCache
import com.tharsis.recipebox.data.model.Recipe

/**
 * Single source of truth the presentation layer talks to.
 *
 * Offline-first: every successful full catalog fetch is written to [RecipeCache].
 * If the API fails (network outage once this is wired to a real backend, or a
 * transient assets read error), the last cached list is served instead.
 */
interface RecipeRepository {
    suspend fun getRecipes(): List<Recipe>
    suspend fun searchRecipes(query: RecipeSearchQuery): List<Recipe>
    suspend fun getRecipeById(id: String): Recipe?
}

class RecipeRepositoryImpl(
    private val api: RecipeApi,
    private val diskCache: RecipeCache,
) : RecipeRepository {

    override suspend fun getRecipes(): List<Recipe> = loadWithCache()

    override suspend fun searchRecipes(query: RecipeSearchQuery): List<Recipe> {
        return runCatching {
            val all = loadWithCache()
            RecipeFilter.apply(all, query)
        }.getOrElse { error ->
            val cached = diskCache.load() ?: throw error
            RecipeFilter.apply(cached, query)
        }
    }

    override suspend fun getRecipeById(id: String): Recipe? {
        return runCatching {
            loadWithCache().firstOrNull { it.id == id }
        }.getOrElse { error ->
            diskCache.load()?.firstOrNull { it.id == id } ?: throw error
        }
    }

    private suspend fun loadWithCache(): List<Recipe> {
        return runCatching {
            api.getRecipes().also { diskCache.save(it) }
        }.getOrElse { error ->
            diskCache.load() ?: throw error
        }
    }
}
