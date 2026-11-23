package com.tharsis.recipebox.ui.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tharsis.recipebox.core.config.AppConfig
import com.tharsis.recipebox.ui.detail.RecipeDetailRoute
import com.tharsis.recipebox.ui.list.RecipeListRoute

/** Centralized route definitions for the app's navigation graph. */
object Routes {
    const val LIST = "recipes"
    const val ARG_RECIPE_ID = "recipeId"
    const val DETAIL = "recipe/{$ARG_RECIPE_ID}"
    fun detail(recipeId: String) = "recipe/$recipeId"
}

/**
 * Hosts navigation inside [SharedTransitionLayout] so the tapped recipe image
 * morphs into the detail hero. Container transitions stay as fades (no swipe
 * slide) so the shared-element motion remains the primary cue.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun RecipeNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val transitionMs = AppConfig.Motion.NAV_TRANSITION_MS

    SharedTransitionLayout(modifier = modifier) {
        NavHost(
            navController = navController,
            startDestination = Routes.LIST,
        ) {
            composable(
                route = Routes.LIST,
                enterTransition = { fadeIn(tween(transitionMs)) },
                exitTransition = { fadeOut(tween(transitionMs)) },
                popEnterTransition = { fadeIn(tween(transitionMs)) },
            ) {
                RecipeListRoute(
                    onRecipeClick = { id -> navController.navigate(Routes.detail(id)) },
                    sharedScope = this@SharedTransitionLayout,
                    animatedScope = this,
                )
            }

            composable(
                route = Routes.DETAIL,
                arguments = listOf(
                    navArgument(Routes.ARG_RECIPE_ID) { type = NavType.StringType },
                ),
                enterTransition = { fadeIn(tween(transitionMs)) },
                exitTransition = { fadeOut(tween(transitionMs)) },
                popExitTransition = { fadeOut(tween(transitionMs)) },
            ) { backStackEntry ->
                val recipeId = backStackEntry.arguments?.getString(Routes.ARG_RECIPE_ID).orEmpty()
                RecipeDetailRoute(
                    recipeId = recipeId,
                    onBack = { navController.popBackStack() },
                    sharedScope = this@SharedTransitionLayout,
                    animatedScope = this,
                )
            }
        }
    }
}
