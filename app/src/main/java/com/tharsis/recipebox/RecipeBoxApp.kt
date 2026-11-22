package com.tharsis.recipebox

import android.app.Application
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.request.crossfade
import com.tharsis.recipebox.di.appModule
import okio.Path.Companion.toOkioPath
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

/**
 * Application entry point. Bootstraps Koin so that the data layer,
 * repositories and ViewModels can be resolved throughout the app.
 */
class RecipeBoxApp : Application() {
    override fun onCreate() {
        super.onCreate()
        SingletonImageLoader.setSafe { context ->
            ImageLoader.Builder(context)
                .memoryCache {
                    MemoryCache.Builder()
                        .maxSizePercent(context, 0.15)
                        .build()
                }
                .diskCache {
                    DiskCache.Builder()
                        .directory(context.cacheDir.resolve("recipe_image_cache").toOkioPath())
                        .maxSizeBytes(64L * 1024L * 1024L)
                        .build()
                }
                .crossfade(180)
                .build()
        }
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@RecipeBoxApp)
            modules(appModule)
        }
    }
}
