package com.tharsis.recipebox

import android.app.Application
import com.tharsis.recipebox.di.appModule
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
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@RecipeBoxApp)
            modules(appModule)
        }
    }
}
