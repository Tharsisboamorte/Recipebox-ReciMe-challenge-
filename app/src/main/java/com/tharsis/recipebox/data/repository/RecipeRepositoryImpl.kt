package com.tharsis.recipebox.data.repository

import com.tharsis.recipebox.data.api.RecipeApi
import com.tharsis.recipebox.data.local.RecipeCache
import com.tharsis.recipebox.domain.model.Recipe
import com.tharsis.recipebox.domain.model.RecipeFilter
import com.tharsis.recipebox.domain.model.RecipeSearchQuery
import com.tharsis.recipebox.domain.repository.RecipeRepository
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Offline-first repository: first successful catalog load is mirrored to disk
 * once; searches use the in-memory API cache (no rewrite per keystroke).
 */
class RecipeRepositoryImpl(
    private val api: RecipeApi,
    private val diskCache: RecipeCache,
) : RecipeRepository {

    private val diskWarmed = AtomicBoolean(false)

    override suspend fun getRecipes(): List<Recipe> = loadCatalog()

    override suspend fun searchRecipes(query: RecipeSearchQuery): List<Recipe> =
        RecipeFilter.apply(loadCatalog(), query)

    override suspend fun getRecipeById(id: String): Recipe? =
        loadCatalog().firstOrNull { it.id == id }

    private suspend fun loadCatalog(): List<Recipe> {
        return runCatching {
            api.getRecipes().also { persistOnce(it) }
        }.getOrElse { error ->
            diskCache.load() ?: throw error
        }
    }

    private suspend fun persistOnce(recipes: List<Recipe>) {
        if (!diskWarmed.compareAndSet(false, true)) return
        runCatching { diskCache.save(recipes) }
            .onFailure { diskWarmed.set(false) }
    }
}
