package com.tharsis.recipebox.data.local

import android.content.Context

/**
 * Reads a bundled asset to a String. Keeps [com.tharsis.recipebox.data.api.LocalRecipeApi]
 * free of Android [Context] so tests can inject fixtures.
 */
fun interface AssetReader {
    fun readText(fileName: String): String
}

class ContextAssetReader(private val context: Context) : AssetReader {
    override fun readText(fileName: String): String =
        context.assets.open(fileName).bufferedReader().use { it.readText() }
}
