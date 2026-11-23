package com.tharsis.recipebox.data.cache

import android.content.Context
import com.tharsis.recipebox.core.config.AppConfig
import com.tharsis.recipebox.data.api.dto.DietaryDto
import com.tharsis.recipebox.data.api.dto.RecipeDto
import com.tharsis.recipebox.data.api.dto.RecipeResponseDto
import com.tharsis.recipebox.data.api.dto.toDomain
import com.tharsis.recipebox.data.model.Recipe
import java.io.File
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

/**
 * Persists the last successfully fetched recipe list to app-private storage.
 *
 * Used as an offline fallback when the primary [com.tharsis.recipebox.data.api.RecipeApi]
 * cannot be reached (e.g. future network-backed API, or a corrupted assets read).
 * Images are separately bundled under `assets/images/` so the UI stays fully usable offline.
 */
class RecipeDiskCache(
    context: Context,
    private val json: Json,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : RecipeCache {
    private val cacheFile: File = File(context.filesDir, CACHE_FILE_NAME)

    override suspend fun save(recipes: List<Recipe>) = withContext(ioDispatcher) {
        val payload = RecipeResponseDto(recipes = recipes.map { it.toDto() })
        cacheFile.writeText(json.encodeToString(RecipeResponseDto.serializer(), payload))
    }

    override suspend fun load(): List<Recipe>? = withContext(ioDispatcher) {
        if (!cacheFile.exists()) return@withContext null
        runCatching {
            json.decodeFromString(RecipeResponseDto.serializer(), cacheFile.readText())
                .recipes
                .map { it.toDomain() }
        }.getOrNull()
    }

    companion object {
        const val CACHE_FILE_NAME = AppConfig.Data.RECIPE_DISK_CACHE_FILE
    }
}

private fun Recipe.toDto(): RecipeDto = RecipeDto(
    id = id,
    title = title,
    description = description,
    servings = servings,
    imageUrl = imageUrl,
    imageAsset = null,
    ingredients = ingredients,
    instructions = instructions,
    dietary = DietaryDto(
        vegetarian = dietary.vegetarian,
        vegan = dietary.vegan,
        glutenFree = dietary.glutenFree,
        dairyFree = dietary.dairyFree,
    ),
)
