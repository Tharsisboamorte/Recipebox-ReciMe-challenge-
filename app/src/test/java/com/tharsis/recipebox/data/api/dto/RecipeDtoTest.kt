package com.tharsis.recipebox.data.api.dto

import com.tharsis.recipebox.data.mapper.resolveImageSource
import com.tharsis.recipebox.data.mapper.toDomain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RecipeDtoTest {

    @Test
    fun `imageAsset takes precedence over imageUrl`() {
        val recipe = RecipeDto(
            id = "r1",
            title = "Pizza",
            imageUrl = "https://example.com/remote.jpg",
            imageAsset = "r1.jpg",
        ).toDomain()

        assertEquals("file:///android_asset/images/r1.jpg", recipe.imageUrl)
    }

    @Test
    fun `falls back to imageUrl when asset is missing`() {
        val recipe = RecipeDto(
            id = "r1",
            title = "Pizza",
            imageUrl = "https://example.com/remote.jpg",
        ).toDomain()

        assertEquals("https://example.com/remote.jpg", recipe.imageUrl)
    }

    @Test
    fun `resolveImageSource returns null when both empty`() {
        assertNull(resolveImageSource(imageAsset = "  ", imageUrl = null))
        assertNull(resolveImageSource(imageAsset = null, imageUrl = ""))
    }
}
