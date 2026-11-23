package com.tharsis.recipebox.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for pure search/filter semantics — no Android dependencies.
 */
class RecipeCatalogFilterTest {

    private val pizza = recipe(
        id = "1",
        title = "Margherita Pizza",
        description = "Cheesy tomato pizza",
        servings = 2,
        ingredients = listOf("Pizza dough", "Tomatoes", "Mozzarella"),
        instructions = listOf("Stretch dough", "Bake until blistered"),
        dietary = DietaryAttributes(vegetarian = true),
    )

    private val curry = recipe(
        id = "2",
        title = "Thai Green Curry",
        description = "Spicy coconut curry with chicken",
        servings = 4,
        ingredients = listOf("Chicken", "Coconut milk", "Green curry paste"),
        instructions = listOf("Fry paste", "Simmer with coconut milk"),
        dietary = DietaryAttributes(),
    )

    private val salad = recipe(
        id = "3",
        title = "Greek Salad",
        description = "Fresh cucumber tomato salad",
        servings = 6,
        ingredients = listOf("Cucumber", "Tomatoes", "Feta", "Olives"),
        instructions = listOf("Chop vegetables", "Toss with oil"),
        dietary = DietaryAttributes(vegetarian = true, glutenFree = true),
    )

    private val all = listOf(pizza, curry, salad)

    @Test
    fun `empty query returns everything`() {
        val result = RecipeCatalogFilter.apply(all, RecipeSearchQuery())
        assertEquals(all, result)
    }

    @Test
    fun `search text matches title case-insensitively`() {
        val result = RecipeCatalogFilter.apply(all, RecipeSearchQuery(searchText = "pizza"))
        assertEquals(listOf(pizza), result)
    }

    @Test
    fun `search text matches instruction content`() {
        val result = RecipeCatalogFilter.apply(all, RecipeSearchQuery(searchText = "simmer"))
        assertEquals(listOf(curry), result)
    }

    @Test
    fun `vegetarian filter excludes non-vegetarian recipes`() {
        val result = RecipeCatalogFilter.apply(all, RecipeSearchQuery(vegetarianOnly = true))
        assertEquals(listOf(pizza, salad), result)
    }

    @Test
    fun `servings bounds are inclusive`() {
        val result = RecipeCatalogFilter.apply(all, RecipeSearchQuery(minServings = 4, maxServings = 6))
        assertEquals(listOf(curry, salad), result)
    }

    @Test
    fun `include requires all ingredients present`() {
        val result = RecipeCatalogFilter.apply(
            all,
            RecipeSearchQuery(includeIngredients = listOf("tomatoes", "mozzarella")),
        )
        assertEquals(listOf(pizza), result)
    }

    @Test
    fun `exclude removes recipes containing any excluded ingredient`() {
        val result = RecipeCatalogFilter.apply(all, RecipeSearchQuery(excludeIngredients = listOf("chicken")))
        assertTrue(result.none { it.id == curry.id })
        assertEquals(listOf(pizza, salad), result)
    }

    @Test
    fun `filters combine with AND semantics`() {
        val result = RecipeCatalogFilter.apply(
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
        imageUri = null,
        ingredients = ingredients,
        instructions = instructions,
        dietary = dietary,
    )
}
