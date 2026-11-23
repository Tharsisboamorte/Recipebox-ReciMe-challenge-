package com.tharsis.recipebox.ui.list

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tharsis.recipebox.R
import com.tharsis.recipebox.ui.theme.RecipeBoxTheme
import com.tharsis.recipebox.ui.theme.Spacing
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

private val ChipShape = RoundedCornerShape(50)
private const val SHEET_HIDE_TIMEOUT_MS = 400L

/**
 * Owns a fresh [rememberModalBottomSheetState] per open.
 *
 * Important: always remove the sheet from composition after dismiss — never gate
 * that on `sheetState.isVisible`. If hide() is interrupted (process churn,
 * SharedTransitionLayout, broken input channel), checking isVisible leaves a
 * stuck overlay that no longer receives touches while clicks fall through.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    filters: RecipeFilters,
    onFiltersChange: (RecipeFilters) -> Unit,
    onClearAll: () -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    fun dismissSheet() {
        scope.launch {
            withTimeoutOrNull(SHEET_HIDE_TIMEOUT_MS) { sheetState.hide() }
            onDismiss()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        FilterSheetContent(
            filters = filters,
            onFiltersChange = onFiltersChange,
            onClearAll = onClearAll,
            onApply = { dismissSheet() },
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterSheetContent(
    modifier: Modifier = Modifier,
    filters: RecipeFilters,
    onFiltersChange: (RecipeFilters) -> Unit,
    onClearAll: () -> Unit,
    onApply: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.xxl)
            .padding(bottom = Spacing.xxl + Spacing.xxs)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(Spacing.xl + Spacing.xxs),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.filter_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            if (filters.hasActiveFilters) {
                TextButton(onClick = onClearAll) {
                    Text(stringResource(R.string.filter_clear), color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        FilterSection(title = stringResource(R.string.filter_section_diet)) {
            FilterChoiceChip(
                label = stringResource(R.string.filter_all),
                selected = !filters.vegetarianOnly,
                onClick = { onFiltersChange(filters.copy(vegetarianOnly = false)) },
            )
            FilterChoiceChip(
                label = stringResource(R.string.filter_vegetarian),
                selected = filters.vegetarianOnly,
                onClick = { onFiltersChange(filters.copy(vegetarianOnly = true)) },
            )
        }

        FilterSection(title = stringResource(R.string.filter_section_servings)) {
            ServingsChip(
                label = stringResource(R.string.filter_servings_any),
                selected = filters.minServings == null && filters.maxServings == null,
                onClick = {
                    onFiltersChange(filters.copy(minServings = null, maxServings = null))
                },
            )
            ServingsChip(
                label = stringResource(R.string.filter_servings_1_2),
                selected = filters.minServings == 1 && filters.maxServings == 2,
                onClick = {
                    onFiltersChange(filters.copy(minServings = 1, maxServings = 2))
                },
            )
            ServingsChip(
                label = stringResource(R.string.filter_servings_3_4),
                selected = filters.minServings == 3 && filters.maxServings == 4,
                onClick = {
                    onFiltersChange(filters.copy(minServings = 3, maxServings = 4))
                },
            )
            ServingsChip(
                label = stringResource(R.string.filter_servings_5_plus),
                selected = filters.minServings == 5 && filters.maxServings == null,
                onClick = {
                    onFiltersChange(filters.copy(minServings = 5, maxServings = null))
                },
            )
        }

        IngredientChipEditor(
            title = stringResource(R.string.filter_must_include),
            hint = stringResource(R.string.filter_include_hint),
            ingredients = filters.includeIngredients,
            onAdd = {
                onFiltersChange(filters.copy(includeIngredients = filters.includeIngredients + it))
            },
            onRemove = {
                onFiltersChange(filters.copy(includeIngredients = filters.includeIngredients - it))
            },
        )

        IngredientChipEditor(
            title = stringResource(R.string.filter_must_exclude),
            hint = stringResource(R.string.filter_exclude_hint),
            ingredients = filters.excludeIngredients,
            onAdd = {
                onFiltersChange(filters.copy(excludeIngredients = filters.excludeIngredients + it))
            },
            onRemove = {
                onFiltersChange(filters.copy(excludeIngredients = filters.excludeIngredients - it))
            },
        )

        Button(
            onClick = onApply,
            modifier = Modifier
                .fillMaxWidth()
                .height(Spacing.filterButtonHeight),
            shape = RoundedCornerShape(Spacing.cardRadius),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        ) {
            Text(stringResource(R.string.filter_apply), fontWeight = FontWeight.SemiBold)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FilterSection(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            content()
        }
    }
}

@Composable
private fun FilterChoiceChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        shape = ChipShape,
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
        ),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.surface,
            labelColor = MaterialTheme.colorScheme.primary,
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
        ),
    )
}

@Composable
private fun ServingsChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChoiceChip(label = label, selected = selected, onClick = onClick)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun IngredientChipEditor(
    title: String,
    hint: String,
    ingredients: List<String>,
    onAdd: (String) -> Unit,
    onRemove: (String) -> Unit,
) {
    var ingredientDraft by remember { mutableStateOf("") }

    fun commit() {
        val value = ingredientDraft.trim()
        if (value.isNotEmpty() && ingredients.none { it.equals(value, ignoreCase = true) }) {
            onAdd(value)
        }
        ingredientDraft = ""
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        OutlinedTextField(
            value = ingredientDraft,
            onValueChange = { ingredientDraft = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text(hint, color = MaterialTheme.colorScheme.onSurfaceVariant) },
            trailingIcon = {
                IconButton(onClick = { commit() }) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = stringResource(R.string.cd_add_ingredient),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { commit() }),
            shape = RoundedCornerShape(Spacing.cardRadius),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                cursorColor = MaterialTheme.colorScheme.primary,
            ),
        )
        if (ingredients.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                ingredients.forEach { ingredient ->
                    InputChip(
                        selected = true,
                        onClick = { onRemove(ingredient) },
                        label = { Text(ingredient) },
                        trailingIcon = {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = stringResource(
                                    R.string.cd_remove_ingredient,
                                    ingredient,
                                ),
                            )
                        },
                        shape = ChipShape,
                        colors = InputChipDefaults.inputChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTrailingIconColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FilterSheetContentPreview() {
    RecipeBoxTheme {
        FilterSheetContent(
            filters = RecipeFilters(
                vegetarianOnly = true,
                minServings = 3,
                maxServings = 4,
                includeIngredients = listOf("garlic"),
                excludeIngredients = listOf("beef"),
            ),
            onFiltersChange = {},
            onClearAll = {},
        )
    }
}
