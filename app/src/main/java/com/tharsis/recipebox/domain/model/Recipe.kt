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
    /** Coil-ready URI: bundled asset (`file:///android_asset/...`) or remote URL. */
    val imageUri: String?,
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
)
