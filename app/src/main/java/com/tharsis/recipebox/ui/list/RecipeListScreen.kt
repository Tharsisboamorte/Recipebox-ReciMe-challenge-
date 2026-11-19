package com.tharsis.recipebox.ui.list

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tharsis.recipebox.ui.components.EmptyResultState
import com.tharsis.recipebox.ui.components.ErrorState
import com.tharsis.recipebox.ui.components.LoadingState
import com.tharsis.recipebox.ui.preview.SampleRecipes
import com.tharsis.recipebox.ui.theme.RecipeBoxTheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import androidx.compose.runtime.collectAsState

/** Stateful entry point wired to Koin; collects state and forwards events. */
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
        topBar = {
            TopAppBar(title = { Text("RecipeBox") })
        },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            RecipeSearchBar(
                searchText = uiState.searchText,
                activeFilterCount = uiState.filters.activeCount,
                onSearchTextChange = onSearchTextChange,
                onClearText = onClearSearchText,
                onFilterClick = { showFilters = true },
            )

            when {
                uiState.isLoading -> LoadingState()
                uiState.errorMessage != null -> ErrorState(
                    message = uiState.errorMessage,
                    onRetry = onRetry,
                )
                uiState.isEmptyResult -> EmptyResultState(onClearFilters = onClearFilters)
                else -> RecipeGrid(
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
                    if (!sheetState.isVisible) showFilters = false
                }
            },
        )
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
        columns = GridCells.Adaptive(minSize = 170.dp),
        state = gridState,
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(items = uiState.recipes, key = { it.id }) { recipe ->
            RecipeCard(
                recipe = recipe,
                onClick = { onRecipeClick(recipe.id) },
                sharedScope = sharedScope,
                animatedScope = animatedScope,
                modifier = Modifier.animateItem(),
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
            onRetry = {},
            onRecipeClick = {},
        )
    }
}
