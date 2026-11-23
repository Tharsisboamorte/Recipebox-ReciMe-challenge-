package com.tharsis.recipebox.domain.model

/**
 * Pure domain recipe. No Android, serialization, or transport annotations —
 * those live on [com.tharsis.recipebox.data.api.dto.RecipeDto].
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
 * Dietary flags a recipe may satisfy. Explicit booleans keep filters type-safe.
 */
data class DietaryAttributes(
    val vegetarian: Boolean = false,
    val vegan: Boolean = false,
    val glutenFree: Boolean = false,
    val dairyFree: Boolean = false,
) {
    /** Stable English keys used by compact card overlays (UI maps chips via strings). */
    val activeLabels: List<String>
        get() = buildList {
            if (vegetarian) add("Vegetarian")
            if (vegan) add("Vegan")
            if (glutenFree) add("Gluten-Free")
            if (dairyFree) add("Dairy-Free")
        }
}
