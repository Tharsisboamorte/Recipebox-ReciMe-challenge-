package com.tharsis.recipebox.data.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Transport models mirroring the (mock) API payload.
 * Mapping to domain lives in [com.tharsis.recipebox.data.mapper].
 */
@Serializable
data class RecipeResponseDto(
    @SerialName("recipes") val recipes: List<RecipeDto> = emptyList(),
)

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
