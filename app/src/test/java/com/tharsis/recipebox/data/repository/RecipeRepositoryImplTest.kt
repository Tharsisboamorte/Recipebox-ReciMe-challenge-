package com.tharsis.recipebox.data.repository

import com.tharsis.recipebox.data.api.RecipeApi
import com.tharsis.recipebox.data.local.RecipeCache
import com.tharsis.recipebox.domain.model.DietaryAttributes
import com.tharsis.recipebox.domain.model.Recipe
import com.tharsis.recipebox.domain.model.RecipeCatalogFilter
import com.tharsis.recipebox.domain.model.RecipeSearchQuery
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
        imageUri = "file:///android_asset/images/r1.jpg",
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
        repo.searchRecipes(RecipeSearchQuery(searchText = "soup"))

        assertTrue("disk must not be rewritten on every search", cache.saved.isEmpty())
    }

    @Test
    fun `getRecipes falls back to disk cache when api fails`() = runTest {
        val api = FakeApi(shouldFail = true)
        val cache = FakeCache(stored = listOf(sample))
        val repo = RecipeRepositoryImpl(api = api, diskCache = cache)

        val result = repo.getRecipes()

        assertEquals(listOf(sample), result)
        assertTrue(cache.saveCount == 0)
    }

    @Test
    fun `searchRecipes filters disk cache when api fails`() = runTest {
        val api = FakeApi(shouldFail = true)
        val cache = FakeCache(stored = listOf(sample))
        val repo = RecipeRepositoryImpl(api = api, diskCache = cache)

        val result = repo.searchRecipes(RecipeSearchQuery(searchText = "soup"))

        assertEquals(1, result.size)
        assertEquals("Cached Soup", result.first().title)
    }

    private class FakeApi(
        private val recipes: List<Recipe> = emptyList(),
        private val shouldFail: Boolean = false,
    ) : RecipeApi {
        override suspend fun getRecipes(): List<Recipe> {
            if (shouldFail) error("offline")
            return recipes
        }

        override suspend fun searchRecipes(query: RecipeSearchQuery): List<Recipe> {
            if (shouldFail) error("offline")
            return RecipeCatalogFilter.apply(recipes, query)
        }

        override suspend fun getRecipeById(id: String): Recipe? {
            if (shouldFail) error("offline")
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
