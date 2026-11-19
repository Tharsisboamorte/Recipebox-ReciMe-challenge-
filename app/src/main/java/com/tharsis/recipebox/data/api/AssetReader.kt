package com.tharsis.recipebox.data.api

import android.content.Context

/**
 * Minimal abstraction over reading a bundled asset to a String. Isolating this
 * behind an interface keeps [LocalRecipeApi] decoupled from the Android
 * framework and makes it trivial to substitute fixtures in tests.
 */
fun interface AssetReader {
    /** Reads the asset at [fileName] (relative to `assets/`) as UTF-8 text. */
    fun readText(fileName: String): String
}

/** Default [AssetReader] backed by the Android [Context] asset manager. */
class ContextAssetReader(private val context: Context) : AssetReader {
    override fun readText(fileName: String): String =
        context.assets.open(fileName).bufferedReader().use { it.readText() }
}
