package com.tharsis.recipebox.data.api

import com.tharsis.recipebox.data.model.DietaryAttributes
import com.tharsis.recipebox.data.model.Recipe
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for the pure search/filter semantics. These exercise the same
 * logic the (mock) search endpoint uses, with no Android dependencies.
 */
class RecipeFilterTest {

    private val pizza = recipe(
        id = "1",
        title = "Margherita Pizza",
        description = "Cheesy tomato pizza",
        servings = 2,
        ingredients = listOf("Pizza dough", "Tomatoes", "Mozzarella"),
        instructions = listOf("Bake until golden"),
        dietary = DietaryAttributes(vegetarian = true),
    )

    private val curry = recipe(
        id = "2",
        title = "Chicken Curry",
        description = "Spicy coconut curry",
        servings = 4,
        ingredients = listOf("Chicken", "Coconut milk", "Garlic"),
        instructions = listOf("Simmer the chicken", "Serve with rice"),
        dietary = DietaryAttributes(vegetarian = false),
    )

    private val salad = recipe(
        id = "3",
        title = "Garden Salad",
        description = "Fresh greens",
        servings = 6,
        ingredients = listOf("Lettuce", "Tomatoes", "Cucumber"),
        instructions = listOf("Toss and serve"),
        dietary = DietaryAttributes(vegetarian = true, vegan = true),
    )

    private val all = listOf(pizza, curry, salad)

    @Test
    fun `empty query returns everything`() {
        val result = RecipeFilter.apply(all, RecipeSearchQuery())
        assertEquals(all, result)
    }

    @Test
    fun `text matches title case-insensitively`() {
        val result = RecipeFilter.apply(all, RecipeSearchQuery(text = "pizza"))
        assertEquals(listOf(pizza), result)
    }

    @Test
    fun `text matches instruction content`() {
        val result = RecipeFilter.apply(all, RecipeSearchQuery(text = "simmer"))
        assertEquals(listOf(curry), result)
    }

    @Test
    fun `vegetarian filter excludes non-vegetarian recipes`() {
        val result = RecipeFilter.apply(all, RecipeSearchQuery(vegetarianOnly = true))
        assertEquals(listOf(pizza, salad), result)
    }

    @Test
    fun `servings bounds are inclusive`() {
        val result = RecipeFilter.apply(all, RecipeSearchQuery(minServings = 4, maxServings = 6))
        assertEquals(listOf(curry, salad), result)
    }

    @Test
    fun `include requires all ingredients present`() {
        val result = RecipeFilter.apply(
            all,
            RecipeSearchQuery(includeIngredients = listOf("tomatoes", "mozzarella")),
        )
        assertEquals(listOf(pizza), result)
    }

    @Test
    fun `exclude removes recipes containing any excluded ingredient`() {
        val result = RecipeFilter.apply(all, RecipeSearchQuery(excludeIngredients = listOf("chicken")))
        assertTrue(result.none { it.id == curry.id })
        assertEquals(listOf(pizza, salad), result)
    }

    @Test
    fun `filters combine with AND semantics`() {
        val result = RecipeFilter.apply(
            all,
            RecipeSearchQuery(
                vegetarianOnly = true,
                includeIngredients = listOf("tomatoes"),
                maxServings = 4,
            ),
        )
        assertEquals(listOf(pizza), result)
    }

    private fun recipe(
        id: String,
        title: String,
        description: String,
        servings: Int,
        ingredients: List<String>,
        instructions: List<String>,
        dietary: DietaryAttributes,
    ) = Recipe(
        id = id,
        title = title,
        description = description,
        servings = servings,
        imageUrl = null,
        ingredients = ingredients,
        instructions = instructions,
        dietary = dietary,
    )
}
