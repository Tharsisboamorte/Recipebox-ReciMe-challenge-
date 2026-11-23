package com.tharsis.recipebox.ui.list

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.tharsis.recipebox.R
import com.tharsis.recipebox.core.AppConfig
import com.tharsis.recipebox.ui.components.EmptyResultState
import com.tharsis.recipebox.ui.components.ErrorState
import com.tharsis.recipebox.ui.components.LoadingState
import com.tharsis.recipebox.ui.preview.SampleRecipes
import com.tharsis.recipebox.ui.theme.RecipeBoxTheme
import com.tharsis.recipebox.ui.theme.Spacing
import org.koin.androidx.compose.koinViewModel

/**
 * Route host: collects state + side effects, wires Android/nav concerns.
 * The dumb [RecipeListScreen] only sees [RecipeListUiState] and [RecipeListIntent].
 */
@Composable
fun RecipeListRoute(
    onRecipeClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    sharedScope: SharedTransitionScope? = null,
    animatedScope: AnimatedVisibilityScope? = null,
    viewModel: RecipeListViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.sideEffects.collect { effect ->
            when (effect) {
                is RecipeListSideEffect.OpenDetail -> onRecipeClick(effect.recipeId)
            }
        }
    }

    RecipeListScreen(
        state = uiState,
        onIntent = viewModel::onIntent,
        modifier = modifier,
        sharedScope = sharedScope,
        animatedScope = animatedScope,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeListScreen(
    state: RecipeListUiState,
    onIntent: (RecipeListIntent) -> Unit,
    modifier: Modifier = Modifier,
    sharedScope: SharedTransitionScope? = null,
    animatedScope: AnimatedVisibilityScope? = null,
) {
    val (showFilters, setShowFilters) = remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.list_title),
                        fontWeight = FontWeight.Bold,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground,
                ),
            )
        },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            RecipeSearchBar(
                searchText = state.searchText,
                activeFilterCount = state.filters.activeCount,
                layoutMode = state.layoutMode,
                onSearchTextChange = { onIntent(RecipeListIntent.SearchChanged(it)) },
                onClearText = { onIntent(RecipeListIntent.ClearSearch) },
                onFilterClick = { setShowFilters(true) },
                onToggleLayout = { onIntent(RecipeListIntent.ToggleLayout) },
            )

            when {
                state.isLoading -> LoadingState()
                state.errorMessage != null -> ErrorState(
                    message = state.errorMessage,
                    onRetry = { onIntent(RecipeListIntent.Retry) },
                )
                state.isEmptyResult -> EmptyResultState(
                    onClearFilters = { onIntent(RecipeListIntent.ClearFilters) },
                )
                else -> RecipeResults(
                    state = state,
                    onIntent = onIntent,
                    sharedScope = sharedScope,
                    animatedScope = animatedScope,
                )
            }
        }
    }

    if (showFilters) {
        FilterBottomSheet(
            filters = state.filters,
            onFiltersChange = { onIntent(RecipeListIntent.FiltersChanged(it)) },
            onClearAll = { onIntent(RecipeListIntent.ClearFilters) },
            onDismiss = { setShowFilters(false) },
        )
    }
}

@Composable
private fun RecipeResults(
    state: RecipeListUiState,
    onIntent: (RecipeListIntent) -> Unit,
    sharedScope: SharedTransitionScope?,
    animatedScope: AnimatedVisibilityScope?,
) {
    val enterMs = AppConfig.Motion.LAYOUT_TRANSITION_MS
    val exitFadeMs = AppConfig.Motion.LAYOUT_EXIT_FADE_MS

    AnimatedContent(
        targetState = state.layoutMode,
        transitionSpec = {
            val forward = targetState == RecipeLayoutMode.List
            if (forward) {
                (slideInHorizontally(tween(enterMs)) { it / 3 } + fadeIn(tween(enterMs))) togetherWith
                    (slideOutHorizontally(tween(enterMs)) { -it / 3 } + fadeOut(tween(exitFadeMs)))
            } else {
                (slideInHorizontally(tween(enterMs)) { -it / 3 } + fadeIn(tween(enterMs))) togetherWith
                    (slideOutHorizontally(tween(enterMs)) { it / 3 } + fadeOut(tween(exitFadeMs)))
            }.using(SizeTransform(clip = false))
        },
        label = "layoutModeTransition",
        modifier = Modifier.fillMaxSize(),
    ) { mode ->
        when (mode) {
            RecipeLayoutMode.Grid -> RecipeGrid(
                state = state,
                onIntent = onIntent,
                sharedScope = sharedScope,
                animatedScope = animatedScope,
            )
            RecipeLayoutMode.List -> RecipeLazyList(
                state = state,
                onIntent = onIntent,
                sharedScope = sharedScope,
                animatedScope = animatedScope,
            )
        }
    }
}

@Composable
private fun RecipeGrid(
    state: RecipeListUiState,
    onIntent: (RecipeListIntent) -> Unit,
    sharedScope: SharedTransitionScope?,
    animatedScope: AnimatedVisibilityScope?,
) {
    val gridState = rememberLazyGridState()
    LazyVerticalGrid(
        columns = GridCells.Fixed(AppConfig.Grid.COLUMN_COUNT),
        state = gridState,
        contentPadding = PaddingValues(Spacing.lg),
        horizontalArrangement = Arrangement.spacedBy(Spacing.md + Spacing.xxs),
        verticalArrangement = Arrangement.spacedBy(Spacing.md + Spacing.xxs),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(items = state.recipes, key = { it.id }) { recipe ->
            RecipeCard(
                recipe = recipe,
                onClick = { onIntent(RecipeListIntent.RecipeClicked(recipe.id)) },
                layoutMode = RecipeLayoutMode.Grid,
                sharedScope = sharedScope,
                animatedScope = animatedScope,
            )
        }
    }
}

@Composable
private fun RecipeLazyList(
    state: RecipeListUiState,
    onIntent: (RecipeListIntent) -> Unit,
    sharedScope: SharedTransitionScope?,
    animatedScope: AnimatedVisibilityScope?,
) {
    val listState = rememberLazyListState()
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(items = state.recipes, key = { it.id }) { recipe ->
            RecipeCard(
                recipe = recipe,
                onClick = { onIntent(RecipeListIntent.RecipeClicked(recipe.id)) },
                layoutMode = RecipeLayoutMode.List,
                sharedScope = sharedScope,
                animatedScope = animatedScope,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RecipeListScreenPreview() {
    RecipeBoxTheme {
        RecipeListScreen(
            state = RecipeListUiState(isLoading = false, recipes = SampleRecipes.all),
            onIntent = {},
        )
    }
}

@Preview(showBackground = true, name = "Empty")
@Composable
private fun RecipeListScreenEmptyPreview() {
    RecipeBoxTheme {
        RecipeListScreen(
            state = RecipeListUiState(
                isLoading = false,
                recipes = emptyList(),
                searchText = "zzz",
            ),
            onIntent = {},
        )
    }
}
