package com.tharsis.recipebox.core

/**
 * Non-UI constants. User-facing copy → `strings.xml`; design tokens → `ui/theme`.
 */
object AppConfig {
    object Data {
        const val RECIPES_ASSET_FILE = "recipes.json"
        const val RECIPE_DISK_CACHE_FILE = "recipes_cache.json"
        const val IMAGE_ASSET_DIR = "images"
        const val IMAGE_ASSET_URI_PREFIX = "file:///android_asset/$IMAGE_ASSET_DIR/"
    }

    object Search {
        const val DEBOUNCE_MS = 250L
        const val STATE_STOP_TIMEOUT_MS = 5_000L
    }

    object Image {
        const val MEMORY_CACHE_PERCENT = 0.15
        const val CROSSFADE_MS = 180
        const val REQUEST_CROSSFADE_MS = 120
        const val THUMBNAIL_WIDTH_PX = 360
        const val THUMBNAIL_HEIGHT_PX = 480
        const val HERO_WIDTH_PX = 900
        const val HERO_HEIGHT_PX = 560
    }

    object Motion {
        const val NAV_TRANSITION_MS = 320
        const val LAYOUT_TRANSITION_MS = 280
        const val LAYOUT_EXIT_FADE_MS = 180
    }

    object Grid {
        const val COLUMN_COUNT = 2
    }
}
