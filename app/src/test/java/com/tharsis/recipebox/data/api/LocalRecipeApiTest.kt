package com.tharsis.recipebox.data.api

import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Verifies the JSON-backed API parses the (fixture) payload, maps DTOs to the
 * domain model, and surfaces clear errors on bad input.
 */
class LocalRecipeApiTest {

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    private val validPayload = """
        {
          "recipes": [
            {
              "id": "r1",
              "title": "Veggie Bowl",
              "description": "Healthy bowl",
              "servings": 2,
              "imageUrl": "https://example.com/img.jpg",
              "ingredients": ["Quinoa", "Spinach"],
              "instructions": ["Cook", "Serve"],
              "dietary": { "vegetarian": true, "vegan": true, "glutenFree": true, "dairyFree": true }
            }
          ]
        }
    """.trimIndent()

    @Test
    fun `parses recipes and maps dietary attributes`() = runTest {
        val api = LocalRecipeApi(assetReader = { validPayload }, json = json)

        val recipes = api.getRecipes()

        assertEquals(1, recipes.size)
        val recipe = recipes.first()
        assertEquals("Veggie Bowl", recipe.title)
        assertEquals(2, recipe.servings)
        assertTrue(recipe.dietary.vegan)
        assertEquals(listOf("Quinoa", "Spinach"), recipe.ingredients)
    }

    @Test
    fun `getRecipeById returns matching recipe or null`() = runTest {
        val api = LocalRecipeApi(assetReader = { validPayload }, json = json)

        assertEquals("Veggie Bowl", api.getRecipeById("r1")?.title)
        assertNull(api.getRecipeById("does-not-exist"))
    }

    @Test
    fun `malformed json throws RecipeDataException`() = runTest {
        val api = LocalRecipeApi(assetReader = { "{ not valid json" }, json = json)

        val error = runCatching { api.getRecipes() }.exceptionOrNull()

        assertTrue(error is RecipeDataException)
    }
}
