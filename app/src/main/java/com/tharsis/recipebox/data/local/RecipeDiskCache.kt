package com.tharsis.recipebox.data.local

import android.content.Context
import com.tharsis.recipebox.core.AppConfig
import com.tharsis.recipebox.data.api.dto.RecipeResponseDto
import com.tharsis.recipebox.data.mapper.toDomain
import com.tharsis.recipebox.data.mapper.toDto
import com.tharsis.recipebox.domain.model.Recipe
import java.io.File
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

/**
 * Last successful catalog in app-private storage.
 * Fallback when the primary API fails; images stay in `assets/images/`.
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
