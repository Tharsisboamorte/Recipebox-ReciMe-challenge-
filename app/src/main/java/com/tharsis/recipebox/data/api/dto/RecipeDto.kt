package com.tharsis.recipebox.data.api.dto

import com.tharsis.recipebox.core.config.AppConfig
import com.tharsis.recipebox.data.model.DietaryAttributes
import com.tharsis.recipebox.data.model.Recipe
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Transport models mirroring the shape of the (mock) API payload.
 *
 * Keeping DTOs separate from the domain [Recipe] means the JSON contract can
 * evolve independently of the UI, and swapping the bundled file for a real
 * endpoint only touches this layer and its mapper.
 */
@Serializable
data class RecipeResponseDto(@SerialName("recipes") val recipes: List<RecipeDto> = emptyList())

@Serializable
data class RecipeDto(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String = "",
    @SerialName("servings") val servings: Int = 1,
    @SerialName("imageUrl") val imageUrl: String? = null,
    /** File name under `assets/images/` (e.g. `r1.jpg`) for offline-first photos. */
    @SerialName("imageAsset") val imageAsset: String? = null,
    @SerialName("ingredients") val ingredients: List<String> = emptyList(),
    @SerialName("instructions") val instructions: List<String> = emptyList(),
    @SerialName("dietary") val dietary: DietaryDto = DietaryDto(),
)

@Serializable
data class DietaryDto(
    @SerialName("vegetarian") val vegetarian: Boolean = false,
    @SerialName("vegan") val vegan: Boolean = false,
    @SerialName("glutenFree") val glutenFree: Boolean = false,
    @SerialName("dairyFree") val dairyFree: Boolean = false,
)

/** Maps a transport [RecipeDto] into the clean domain [Recipe]. */
fun RecipeDto.toDomain(): Recipe = Recipe(
    id = id,
    title = title,
    description = description,
    servings = servings,
    imageUrl = resolveImageSource(imageAsset = imageAsset, imageUrl = imageUrl),
    ingredients = ingredients,
    instructions = instructions,
    dietary = DietaryAttributes(
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
