package com.tharsis.recipebox.data.repository

import com.tharsis.recipebox.data.api.RecipeApi
import com.tharsis.recipebox.data.api.RecipeFilter
import com.tharsis.recipebox.data.api.RecipeSearchQuery
import com.tharsis.recipebox.data.cache.RecipeCache
import com.tharsis.recipebox.data.model.DietaryAttributes
import com.tharsis.recipebox.data.model.Recipe
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RecipeRepositoryImplTest {

    private val sample = Recipe(
        id = "r1",
        title = "Cached Soup",
        description = "Warm",
        servings = 2,
        imageUrl = "file:///android_asset/images/r1.jpg",
        ingredients = listOf("Water"),
        instructions = listOf("Boil"),
        dietary = DietaryAttributes(
            vegetarian = true,
            vegan = true,
            glutenFree = true,
            dairyFree = true,
        ),
    )

    @Test
    fun `getRecipes saves successful api response to disk cache once`() = runTest {
        val api = FakeApi(recipes = listOf(sample))
        val cache = FakeCache()
        val repo = RecipeRepositoryImpl(api = api, diskCache = cache)

        assertEquals(listOf(sample), repo.getRecipes())
        assertEquals(listOf(sample), cache.saved)

        cache.saved = emptyList()
        repo.getRecipes()
        repo.searchRecipes(RecipeSearchQuery(text = "soup"))

        assertTrue("disk must not be rewritten on every search", cache.saved.isEmpty())
    }

    @Test
    fun `getRecipes falls back to disk cache when api fails`() = runTest {
        val api = FakeApi(fail = true)
        val cache = FakeCache(stored = listOf(sample))
        val repo = RecipeRepositoryImpl(api = api, diskCache = cache)

        val result = repo.getRecipes()

        assertEquals(listOf(sample), result)
        assertTrue(cache.saveCount == 0)
    }

    @Test
    fun `searchRecipes filters disk cache when api fails`() = runTest {
        val api = FakeApi(fail = true)
        val cache = FakeCache(stored = listOf(sample))
        val repo = RecipeRepositoryImpl(api = api, diskCache = cache)

        val result = repo.searchRecipes(RecipeSearchQuery(text = "soup"))

        assertEquals(1, result.size)
        assertEquals("Cached Soup", result.first().title)
    }

    private class FakeApi(private val recipes: List<Recipe> = emptyList(), private val fail: Boolean = false) :
        RecipeApi {
        override suspend fun getRecipes(): List<Recipe> {
            if (fail) error("offline")
            return recipes
        }

        override suspend fun searchRecipes(query: RecipeSearchQuery): List<Recipe> {
            if (fail) error("offline")
            return RecipeFilter.apply(recipes, query)
        }

        override suspend fun getRecipeById(id: String): Recipe? {
            if (fail) error("offline")
            return recipes.firstOrNull { it.id == id }
        }
    }

    private class FakeCache(private var stored: List<Recipe>? = null) : RecipeCache {
        var saved: List<Recipe> = emptyList()
        var saveCount: Int = 0
            private set

        override suspend fun save(recipes: List<Recipe>) {
            saveCount++
            saved = recipes
            stored = recipes
        }

        override suspend fun load(): List<Recipe>? = stored
    }
}
