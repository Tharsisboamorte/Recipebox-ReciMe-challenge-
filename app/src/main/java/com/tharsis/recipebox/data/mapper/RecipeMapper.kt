package com.tharsis.recipebox.data.mapper

import com.tharsis.recipebox.core.AppConfig
import com.tharsis.recipebox.data.api.dto.DietaryDto
import com.tharsis.recipebox.data.api.dto.RecipeDto
import com.tharsis.recipebox.domain.model.DietaryAttributes
import com.tharsis.recipebox.domain.model.Recipe

/** Maps transport DTOs ↔ domain models. */
fun RecipeDto.toDomain(): Recipe = Recipe(
    id = id,
    title = title,
    description = description,
    servings = servings,
    imageUri = resolveImageSource(imageAsset = imageAsset, imageUrl = imageUrl),
    ingredients = ingredients,
    instructions = instructions,
    dietary = DietaryAttributes(
        vegetarian = dietary.vegetarian,
        vegan = dietary.vegan,
        glutenFree = dietary.glutenFree,
        dairyFree = dietary.dairyFree,
    ),
)

fun Recipe.toDto(): RecipeDto = RecipeDto(
    id = id,
    title = title,
    description = description,
    servings = servings,
    imageUrl = imageUri,
    imageAsset = null,
    ingredients = ingredients,
    instructions = instructions,
    dietary = DietaryDto(
        vegetarian = dietary.vegetarian,
        vegan = dietary.vegan,
        glutenFree = dietary.glutenFree,
        dairyFree = dietary.dairyFree,
    ),
)

/**
 * Prefers a bundled asset (works offline) and falls back to a remote URL.
 * Coil loads assets via the `file:///android_asset/...` scheme.
 */
fun resolveImageSource(imageAsset: String?, imageUrl: String?): String? {
    val asset = imageAsset?.trim().orEmpty()
    if (asset.isNotEmpty()) {
        val fileName = asset
            .removePrefix("${AppConfig.Data.IMAGE_ASSET_DIR}/")
            .removePrefix("images/")
        return AppConfig.Data.IMAGE_ASSET_URI_PREFIX + fileName
    }
    return imageUrl?.takeIf { it.isNotBlank() }
}
