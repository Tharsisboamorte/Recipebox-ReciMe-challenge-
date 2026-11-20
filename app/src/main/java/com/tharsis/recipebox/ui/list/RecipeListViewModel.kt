package com.tharsis.recipebox.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tharsis.recipebox.data.model.Recipe
import com.tharsis.recipebox.data.repository.RecipeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

/**
 * Drives the recipe list screen. Owns the search text and filter state, turns
 * them into a debounced [com.tharsis.recipebox.data.api.RecipeSearchQuery], and
 * exposes a single [RecipeListUiState] stream for the UI to observe.
 */
@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class RecipeListViewModel(
    private val repository: RecipeRepository,
) : ViewModel() {

    private val searchText = MutableStateFlow("")
    private val filters = MutableStateFlow(RecipeFilters())
    private val refreshTrigger = MutableStateFlow(0)

    private val loadState: Flow<LoadState> =
        combine(searchText, filters, refreshTrigger) { text, filters, _ ->
            filters.toQuery(text)
        }
            .debounce(SEARCH_DEBOUNCE_MS)
            .distinctUntilChanged()
            .mapLatest { query ->
                runCatching { repository.searchRecipes(query) }
                    .fold(
                        onSuccess = { LoadState.Success(it) },
                        onFailure = { LoadState.Error(it.message ?: DEFAULT_ERROR) },
                    )
            }
            .onStart { emit(LoadState.Loading) }

    val uiState: StateFlow<RecipeListUiState> =
        combine(loadState, searchText, filters) { state, text, activeFilters ->
            RecipeListUiState(
                isLoading = state is LoadState.Loading,
                recipes = (state as? LoadState.Success)?.recipes.orEmpty(),
                errorMessage = (state as? LoadState.Error)?.message,
                searchText = text,
                filters = activeFilters,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
            initialValue = RecipeListUiState(isLoading = true),
        )

    fun onSearchTextChange(text: String) {
        searchText.value = text
    }

    fun onFiltersChange(newFilters: RecipeFilters) {
        filters.value = newFilters
    }

    fun clearFilters() {
        filters.value = RecipeFilters()
    }

    fun clearSearchText() {
        searchText.value = ""
    }

    fun retry() {
        refreshTrigger.update { it + 1 }
    }

    private sealed interface LoadState {
        data object Loading : LoadState
        data class Success(val recipes: List<Recipe>) : LoadState
        data class Error(val message: String) : LoadState
    }

    private companion object {
        const val SEARCH_DEBOUNCE_MS = 250L
        const val STOP_TIMEOUT_MS = 5_000L
        const val DEFAULT_ERROR = "Something went wrong loading recipes."
    }
}
