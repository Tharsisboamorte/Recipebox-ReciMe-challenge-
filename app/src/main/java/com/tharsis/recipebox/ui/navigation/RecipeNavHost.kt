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
import com.tharsis.recipebox.ui.detail.RecipeDetailRoute
import com.tharsis.recipebox.ui.list.RecipeListRoute

/** Centralized route definitions for the app's navigation graph. */
object Routes {
    const val LIST = "recipes"
    const val ARG_RECIPE_ID = "recipeId"
    const val DETAIL = "recipe/{$ARG_RECIPE_ID}"
    fun detail(recipeId: String) = "recipe/$recipeId"
}

private const val TRANSITION_MS = 320

/**
 * Hosts navigation inside [SharedTransitionLayout] so the tapped recipe image
 * morphs into the detail hero. Container transitions stay as fades (no swipe
 * slide) so the shared-element motion remains the primary cue.
 *
 * Image decode sizes stay capped (see [com.tharsis.recipebox.ui.components.RecipeImageSize])
 * to keep scroll memory under control while shared elements are active.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun RecipeNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    SharedTransitionLayout(modifier = modifier) {
        NavHost(
            navController = navController,
            startDestination = Routes.LIST,
        ) {
            composable(
                route = Routes.LIST,
                enterTransition = { fadeIn(tween(TRANSITION_MS)) },
                exitTransition = { fadeOut(tween(TRANSITION_MS)) },
                popEnterTransition = { fadeIn(tween(TRANSITION_MS)) },
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
                enterTransition = { fadeIn(tween(TRANSITION_MS)) },
                exitTransition = { fadeOut(tween(TRANSITION_MS)) },
                popExitTransition = { fadeOut(tween(TRANSITION_MS)) },
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
