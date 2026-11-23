package com.tharsis.recipebox.data.repository

import com.tharsis.recipebox.data.api.RecipeApi
import com.tharsis.recipebox.data.api.RecipeFilter
import com.tharsis.recipebox.data.api.RecipeSearchQuery
import com.tharsis.recipebox.data.cache.RecipeCache
import com.tharsis.recipebox.data.model.Recipe
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Single source of truth the presentation layer talks to.
 *
 * Offline-first: the first successful catalog load is mirrored to [RecipeCache].
 * Subsequent searches hit the in-memory API cache only — rewriting disk on every
 * keystroke caused heavy I/O wait and ANRs on constrained emulators.
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

    private val diskWarmed = AtomicBoolean(false)

    override suspend fun getRecipes(): List<Recipe> = loadCatalog()

    override suspend fun searchRecipes(query: RecipeSearchQuery): List<Recipe> {
        val catalog = loadCatalog()
        return RecipeFilter.apply(catalog, query)
    }

    override suspend fun getRecipeById(id: String): Recipe? =
        loadCatalog().firstOrNull { it.id == id }

    private suspend fun loadCatalog(): List<Recipe> {
        return runCatching {
            api.getRecipes().also { persistOnce(it) }
        }.getOrElse { error ->
            diskCache.load() ?: throw error
        }
    }

    /** Write disk at most once per process; never on the hot search path repeatedly. */
    private suspend fun persistOnce(recipes: List<Recipe>) {
        if (!diskWarmed.compareAndSet(false, true)) return
        runCatching { diskCache.save(recipes) }
            .onFailure { diskWarmed.set(false) }
    }
}
