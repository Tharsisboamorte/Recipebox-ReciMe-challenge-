package com.tharsis.recipebox.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tharsis.recipebox.domain.model.Recipe
import com.tharsis.recipebox.domain.repository.RecipeRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Immutable snapshot for the detail screen. */
data class RecipeDetailUiState(
    val isLoading: Boolean = true,
    val recipe: Recipe? = null,
    val errorMessage: String? = null,
)

/**
 * Loads a single recipe by id. The id is injected by Koin from the navigation
 * argument so the ViewModel stays testable and free of nav dependencies.
 * Events enter via [onIntent]; navigation leaves via [sideEffects].
 */
class RecipeDetailViewModel(private val repository: RecipeRepository, private val recipeId: String) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeDetailUiState())
    val uiState: StateFlow<RecipeDetailUiState> = _uiState.asStateFlow()

    private val _sideEffects = Channel<RecipeDetailSideEffect>(Channel.BUFFERED)
    val sideEffects: Flow<RecipeDetailSideEffect> = _sideEffects.receiveAsFlow()

    init {
        onIntent(RecipeDetailIntent.Load)
    }

    fun onIntent(intent: RecipeDetailIntent) {
        when (intent) {
            RecipeDetailIntent.Load,
            RecipeDetailIntent.Retry,
            -> load()
            RecipeDetailIntent.Back -> viewModelScope.launch {
                _sideEffects.send(RecipeDetailSideEffect.NavigateBack)
            }
        }
    }

    private fun load() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            runCatching { repository.getRecipeById(recipeId) }
                .onSuccess { recipe ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            recipe = recipe,
                            errorMessage = if (recipe == null) NOT_FOUND else null,
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: LOAD_FAILED,
                        )
                    }
                }
        }
    }

    private companion object {
        const val NOT_FOUND = "Recipe not found."
        const val LOAD_FAILED = "Couldn't load this recipe."
    }
}
