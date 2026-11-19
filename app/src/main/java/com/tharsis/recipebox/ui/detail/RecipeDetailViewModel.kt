package com.tharsis.recipebox.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tharsis.recipebox.data.model.Recipe
import com.tharsis.recipebox.data.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** State for the detail screen. */
data class RecipeDetailUiState(
    val isLoading: Boolean = true,
    val recipe: Recipe? = null,
    val errorMessage: String? = null,
)

/**
 * Loads a single recipe by id. The id is injected by Koin from the navigation
 * argument so the ViewModel stays testable and free of nav dependencies.
 */
class RecipeDetailViewModel(
    private val repository: RecipeRepository,
    private val recipeId: String,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeDetailUiState())
    val uiState: StateFlow<RecipeDetailUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            runCatching { repository.getRecipeById(recipeId) }
                .onSuccess { recipe ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            recipe = recipe,
                            errorMessage = if (recipe == null) "Recipe not found." else null,
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Couldn't load this recipe.",
                        )
                    }
                }
        }
    }
}
