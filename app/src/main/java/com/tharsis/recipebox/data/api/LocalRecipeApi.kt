package com.tharsis.recipebox.data.api

import com.tharsis.recipebox.data.api.dto.RecipeResponseDto
import com.tharsis.recipebox.data.api.dto.toDomain
import com.tharsis.recipebox.data.model.Recipe
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

/**
 * [RecipeApi] implementation backed by a JSON file bundled in `assets/`.
 *
 * The file is parsed once on first access and cached in memory, simulating a
 * network call that is served from a local fixture. Parsing/IO run on the IO
 * dispatcher; filtering reuses the pure [RecipeFilter] so behavior is identical
 * to what a server-side search would produce.
 */
class LocalRecipeApi(
    private val assetReader: AssetReader,
    private val json: Json,
    private val fileName: String = DEFAULT_FILE_NAME,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : RecipeApi {

    private val cacheMutex = Mutex()
    @Volatile
    private var cache: List<Recipe>? = null

    override suspend fun getRecipes(): List<Recipe> = loadRecipes()

    override suspend fun searchRecipes(query: RecipeSearchQuery): List<Recipe> =
        RecipeFilter.apply(loadRecipes(), query)

    override suspend fun getRecipeById(id: String): Recipe? =
        loadRecipes().firstOrNull { it.id == id }

    private suspend fun loadRecipes(): List<Recipe> {
        cache?.let { return it }
        return cacheMutex.withLock {
            cache ?: parseFromAssets().also { cache = it }
        }
    }

    private suspend fun parseFromAssets(): List<Recipe> = withContext(ioDispatcher) {
        val raw = runCatching { assetReader.readText(fileName) }
            .getOrElse { throw RecipeDataException("Unable to read \"$fileName\" from assets.", it) }

        val response = runCatching { json.decodeFromString<RecipeResponseDto>(raw) }
            .getOrElse { throw RecipeDataException("Recipe data in \"$fileName\" is malformed.", it) }

        response.recipes.map { it.toDomain() }
    }

    companion object {
        const val DEFAULT_FILE_NAME = "recipes.json"
    }
}

/** Raised when bundled recipe data cannot be read or parsed. */
class RecipeDataException(message: String, cause: Throwable? = null) : Exception(message, cause)
