package com.tharsis.recipebox.data.api.dto

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
    imageUrl = imageUrl,
    ingredients = ingredients,
    instructions = instructions,
    dietary = DietaryAttributes(
        vegetarian = dietary.vegetarian,
        vegan = dietary.vegan,
        glutenFree = dietary.glutenFree,
        dairyFree = dietary.dairyFree,
    ),
)
