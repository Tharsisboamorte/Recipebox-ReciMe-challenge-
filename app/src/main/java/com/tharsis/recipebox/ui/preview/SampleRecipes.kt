package com.tharsis.recipebox.ui.preview

import com.tharsis.recipebox.domain.model.DietaryAttributes
import com.tharsis.recipebox.domain.model.Recipe

/**
 * Static sample data used exclusively by `@Preview` composables so previews
 * render without Koin, I/O or a running ViewModel.
 */
object SampleRecipes {

    val margherita = Recipe(
        id = "r1",
        title = "Classic Margherita Pizza",
        description = "A simple Neapolitan-style pizza with San Marzano tomatoes, fresh mozzarella, and basil.",
        servings = 2,
        imageUrl = null,
        ingredients = listOf(
            "250g pizza dough",
            "100g San Marzano tomatoes",
            "125g fresh mozzarella",
            "Fresh basil leaves",
            "2 tbsp olive oil",
        ),
        instructions = listOf(
            "Preheat your oven to its highest setting with a pizza stone inside.",
            "Stretch the dough into a 10-inch round on a floured surface.",
            "Spread crushed tomatoes over the base, leaving a border for the crust.",
            "Bake for 7-9 minutes until the crust is blistered and golden.",
        ),
        dietary = DietaryAttributes(vegetarian = true),
    )

    val curry = Recipe(
        id = "r2",
        title = "Thai Green Curry with Chicken",
        description = "Fragrant coconut curry with tender chicken, Thai basil, and crisp vegetables.",
        servings = 4,
        imageUrl = null,
        ingredients = listOf(
            "500g chicken thigh, sliced",
            "3 tbsp green curry paste",
            "400ml coconut milk",
        ),
        instructions = listOf(
            "Fry the curry paste until fragrant.",
            "Add the chicken and cook until it changes color.",
            "Pour in the coconut milk and simmer.",
        ),
        dietary = DietaryAttributes(glutenFree = true, dairyFree = true),
    )

    val buddhaBowl = Recipe(
        id = "r14",
        title = "Roasted Vegetable Buddha Bowl",
        description = "A nourishing bowl of roasted veg, quinoa, and lemon-tahini dressing.",
        servings = 2,
        imageUrl = null,
        ingredients = listOf("150g quinoa", "1 sweet potato", "1 can chickpeas"),
        instructions = listOf("Roast the vegetables.", "Cook the quinoa.", "Assemble and dress."),
        dietary = DietaryAttributes(vegetarian = true, vegan = true, glutenFree = true, dairyFree = true),
    )

    val all: List<Recipe> = listOf(margherita, curry, buddhaBowl)
}
