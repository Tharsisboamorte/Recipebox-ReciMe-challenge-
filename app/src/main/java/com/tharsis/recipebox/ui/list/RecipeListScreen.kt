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
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tharsis.recipebox.ui.components.EmptyResultState
import com.tharsis.recipebox.ui.components.ErrorState
import com.tharsis.recipebox.ui.components.LoadingState
import com.tharsis.recipebox.ui.preview.SampleRecipes
import com.tharsis.recipebox.ui.theme.RecipeBoxTheme
import com.tharsis.recipebox.ui.theme.TextPrimary
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun RecipeListRoute(
    onRecipeClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    sharedScope: SharedTransitionScope? = null,
    animatedScope: AnimatedVisibilityScope? = null,
    viewModel: RecipeListViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    RecipeListScreen(
        uiState = uiState,
        onSearchTextChange = viewModel::onSearchTextChange,
        onClearSearchText = viewModel::clearSearchText,
        onFiltersChange = viewModel::onFiltersChange,
        onClearFilters = viewModel::clearFilters,
        onToggleLayout = viewModel::toggleLayoutMode,
        onRetry = viewModel::retry,
        onRecipeClick = onRecipeClick,
        modifier = modifier,
        sharedScope = sharedScope,
        animatedScope = animatedScope,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeListScreen(
    uiState: RecipeListUiState,
    onSearchTextChange: (String) -> Unit,
    onClearSearchText: () -> Unit,
    onFiltersChange: (RecipeFilters) -> Unit,
    onClearFilters: () -> Unit,
    onToggleLayout: () -> Unit,
    onRetry: () -> Unit,
    onRecipeClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    sharedScope: SharedTransitionScope? = null,
    animatedScope: AnimatedVisibilityScope? = null,
) {
    var showFilters by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Search recipes",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            RecipeSearchBar(
                searchText = uiState.searchText,
                activeFilterCount = uiState.filters.activeCount,
                layoutMode = uiState.layoutMode,
                onSearchTextChange = onSearchTextChange,
                onClearText = onClearSearchText,
                onFilterClick = { showFilters = true },
                onToggleLayout = onToggleLayout,
            )

            when {
                uiState.isLoading -> LoadingState()
                uiState.errorMessage != null -> ErrorState(
                    message = uiState.errorMessage,
                    onRetry = onRetry,
                )
                uiState.isEmptyResult -> EmptyResultState(onClearFilters = onClearFilters)
                else -> RecipeResults(
                    uiState = uiState,
                    onRecipeClick = onRecipeClick,
                    sharedScope = sharedScope,
                    animatedScope = animatedScope,
                )
            }
        }
    }

    if (showFilters) {
        FilterBottomSheet(
            filters = uiState.filters,
            sheetState = sheetState,
            onFiltersChange = onFiltersChange,
            onClearAll = onClearFilters,
            onDismiss = {
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    //TODO: FIX THIS UNUSED VARIABLE
                    if (!sheetState.isVisible) showFilters = false
                }
            },
        )
    }
}

@Composable
private fun RecipeResults(
    uiState: RecipeListUiState,
    onRecipeClick: (String) -> Unit,
    sharedScope: SharedTransitionScope?,
    animatedScope: AnimatedVisibilityScope?,
) {
    AnimatedContent(
        targetState = uiState.layoutMode,
        transitionSpec = {
            val forward = targetState == RecipeLayoutMode.List
            if (forward) {
                (slideInHorizontally(tween(280)) { it / 3 } + fadeIn(tween(280))) togetherWith
                    (slideOutHorizontally(tween(280)) { -it / 3 } + fadeOut(tween(180)))
            } else {
                (slideInHorizontally(tween(280)) { -it / 3 } + fadeIn(tween(280))) togetherWith
                    (slideOutHorizontally(tween(280)) { it / 3 } + fadeOut(tween(180)))
            }.using(SizeTransform(clip = false))
        },
        label = "layoutModeTransition",
        modifier = Modifier.fillMaxSize(),
    ) { mode ->
        when (mode) {
            RecipeLayoutMode.Grid -> RecipeGrid(
                uiState = uiState,
                onRecipeClick = onRecipeClick,
                sharedScope = sharedScope,
                animatedScope = animatedScope,
            )
            RecipeLayoutMode.List -> RecipeLazyList(
                uiState = uiState,
                onRecipeClick = onRecipeClick,
                sharedScope = sharedScope,
                animatedScope = animatedScope,
            )
        }
    }
}

@Composable
private fun RecipeGrid(
    uiState: RecipeListUiState,
    onRecipeClick: (String) -> Unit,
    sharedScope: SharedTransitionScope?,
    animatedScope: AnimatedVisibilityScope?,
) {
    val gridState = rememberLazyGridState()
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = gridState,
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(items = uiState.recipes, key = { it.id }) { recipe ->
            RecipeCard(
                recipe = recipe,
                onClick = { onRecipeClick(recipe.id) },
                layoutMode = RecipeLayoutMode.Grid,
                sharedScope = sharedScope,
                animatedScope = animatedScope,
            )
        }
    }
}

@Composable
private fun RecipeLazyList(
    uiState: RecipeListUiState,
    onRecipeClick: (String) -> Unit,
    sharedScope: SharedTransitionScope?,
    animatedScope: AnimatedVisibilityScope?,
) {
    val listState = rememberLazyListState()
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(items = uiState.recipes, key = { it.id }) { recipe ->
            RecipeCard(
                recipe = recipe,
                onClick = { onRecipeClick(recipe.id) },
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
            uiState = RecipeListUiState(isLoading = false, recipes = SampleRecipes.all),
            onSearchTextChange = {},
            onClearSearchText = {},
            onFiltersChange = {},
            onClearFilters = {},
            onToggleLayout = {},
            onRetry = {},
            onRecipeClick = {},
        )
    }
}

@Preview(showBackground = true, name = "Empty")
@Composable
private fun RecipeListScreenEmptyPreview() {
    RecipeBoxTheme {
        RecipeListScreen(
            uiState = RecipeListUiState(isLoading = false, recipes = emptyList(), searchText = "zzz"),
            onSearchTextChange = {},
            onClearSearchText = {},
            onFiltersChange = {},
            onClearFilters = {},
            onToggleLayout = {},
            onRetry = {},
            onRecipeClick = {},
        )
    }
}
