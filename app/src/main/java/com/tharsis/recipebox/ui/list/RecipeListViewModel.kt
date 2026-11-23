package com.tharsis.recipebox.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tharsis.recipebox.core.config.AppConfig
import com.tharsis.recipebox.data.model.Recipe
import com.tharsis.recipebox.data.repository.RecipeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * List screen reducer. All UI events enter through [onIntent]; one-shot
 * navigation leaves through [sideEffects] (ViaUnica-style MVI, single-module).
 */
@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class RecipeListViewModel(private val repository: RecipeRepository) : ViewModel() {

    private val searchText = MutableStateFlow("")
    private val filters = MutableStateFlow(RecipeFilters())
    private val layoutMode = MutableStateFlow(RecipeLayoutMode.Grid)
    private val refreshTrigger = MutableStateFlow(0)

    private val _sideEffects = Channel<RecipeListSideEffect>(Channel.BUFFERED)
    val sideEffects: Flow<RecipeListSideEffect> = _sideEffects.receiveAsFlow()

    private val loadState: Flow<LoadState> =
        combine(searchText, filters, refreshTrigger) { text, activeFilters, _ ->
            activeFilters.toQuery(text)
        }
            .debounce(AppConfig.Search.DEBOUNCE_MS)
            .distinctUntilChanged()
            .mapLatest { query ->
                runCatching { repository.searchRecipes(query) }
                    .fold(
                        onSuccess = { LoadState.Success(it) },
                        onFailure = { LoadState.Error(it.message ?: GENERIC_ERROR) },
                    )
            }

    val uiState: StateFlow<RecipeListUiState> =
        combine(loadState, searchText, filters, layoutMode) { state, text, activeFilters, layout ->
            RecipeListUiState(
                isLoading = state is LoadState.Loading,
                recipes = (state as? LoadState.Success)?.recipes.orEmpty(),
                errorMessage = (state as? LoadState.Error)?.message,
                searchText = text,
                filters = activeFilters,
                layoutMode = layout,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(AppConfig.Search.STATE_STOP_TIMEOUT_MS),
            initialValue = RecipeListUiState(isLoading = true),
        )

    fun onIntent(intent: RecipeListIntent) {
        when (intent) {
            is RecipeListIntent.SearchChanged -> searchText.value = intent.text
            RecipeListIntent.ClearSearch -> searchText.value = ""
            is RecipeListIntent.FiltersChanged -> filters.value = intent.filters
            RecipeListIntent.ClearFilters -> filters.value = RecipeFilters()
            RecipeListIntent.ToggleLayout -> layoutMode.update { current ->
                if (current == RecipeLayoutMode.Grid) {
                    RecipeLayoutMode.List
                } else {
                    RecipeLayoutMode.Grid
                }
            }
            RecipeListIntent.Retry -> refreshTrigger.update { it + 1 }
            is RecipeListIntent.RecipeClicked -> viewModelScope.launch {
                _sideEffects.send(RecipeListSideEffect.OpenDetail(intent.recipeId))
            }
        }
    }

    private sealed interface LoadState {
        data object Loading : LoadState
        data class Success(val recipes: List<Recipe>) : LoadState
        data class Error(val message: String) : LoadState
    }

    private companion object {
        const val GENERIC_ERROR = "Something went wrong loading recipes."
    }
}
