package com.tharsis.recipebox.di

import com.tharsis.recipebox.data.api.AssetReader
import com.tharsis.recipebox.data.api.ContextAssetReader
import com.tharsis.recipebox.data.api.LocalRecipeApi
import com.tharsis.recipebox.data.api.RecipeApi
import com.tharsis.recipebox.data.cache.RecipeCache
import com.tharsis.recipebox.data.cache.RecipeDiskCache
import com.tharsis.recipebox.data.repository.RecipeRepository
import com.tharsis.recipebox.data.repository.RecipeRepositoryImpl
import com.tharsis.recipebox.ui.detail.RecipeDetailViewModel
import com.tharsis.recipebox.ui.list.RecipeListViewModel
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Koin graph for the whole app. Interfaces are bound to their implementations
 * here so swapping, e.g., [LocalRecipeApi] for a network-backed one is a
 * one-line change with no impact on consumers.
 */
val appModule = module {
    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
    }

    single<AssetReader> { ContextAssetReader(androidContext()) }

    single<RecipeCache> { RecipeDiskCache(context = androidContext(), json = get()) }

    single<RecipeApi> { LocalRecipeApi(assetReader = get(), json = get()) }

    single<RecipeRepository> { RecipeRepositoryImpl(api = get(), diskCache = get()) }

    viewModel { RecipeListViewModel(repository = get()) }

    viewModel { (recipeId: String) ->
        RecipeDetailViewModel(repository = get(), recipeId = recipeId)
    }
}
