package com.tharsis.recipebox.data.model

/**
 * Domain representation of a cooking recipe.
 *
 * This is intentionally decoupled from any transport/serialization concern
 * (see [com.tharsis.recipebox.data.api.dto.RecipeDto]) so that the rest of the
 * app depends only on a clean domain type regardless of where the data
 * originates (bundled JSON today, a real network API tomorrow).
 */
data class Recipe(
    val id: String,
    val title: String,
    val description: String,
    val servings: Int,
    val imageUrl: String?,
    val ingredients: List<String>,
    val instructions: List<String>,
    val dietary: DietaryAttributes,
)

/**
 * Dietary flags a recipe may satisfy. Modeled explicitly rather than as a
 * free-form string list so filters stay type-safe and self-documenting.
 */
data class DietaryAttributes(
    val vegetarian: Boolean = false,
    val vegan: Boolean = false,
    val glutenFree: Boolean = false,
    val dairyFree: Boolean = false,
) {
    /** Human-readable labels for the attributes this recipe satisfies. */
    val activeLabels: List<String>
        get() = buildList {
            if (vegetarian) add("Vegetarian")
            if (vegan) add("Vegan")
            if (glutenFree) add("Gluten-Free")
            if (dairyFree) add("Dairy-Free")
        }
}
