package com.tharsis.recipebox.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tharsis.recipebox.ui.theme.RecipeBoxTheme
import kotlin.math.roundToInt

private const val SERVINGS_MIN = 1f
private const val SERVINGS_MAX = 12f

/**
 * Modal bottom sheet hosting all filter controls. Changes are applied live to
 * keep results responsive as the user tweaks filters.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    filters: RecipeFilters,
    sheetState: SheetState,
    onFiltersChange: (RecipeFilters) -> Unit,
    onClearAll: () -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        FilterSheetContent(
            filters = filters,
            onFiltersChange = onFiltersChange,
            onClearAll = onClearAll,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterSheetContent(
    filters: RecipeFilters,
    onFiltersChange: (RecipeFilters) -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(bottom = 24.dp)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Filters", style = MaterialTheme.typography.titleLarge)
            if (filters.hasAny) {
                TextButton(onClick = onClearAll) { Text("Clear all") }
            }
        }

        // Vegetarian toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text("Vegetarian only", style = MaterialTheme.typography.bodyLarge)
                Text(
                    "Show only vegetarian recipes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Switch(
                checked = filters.vegetarianOnly,
                onCheckedChange = { onFiltersChange(filters.copy(vegetarianOnly = it)) },
            )
        }

        // Servings range
        ServingsFilter(filters = filters, onFiltersChange = onFiltersChange)

        // Include ingredients
        IngredientFilter(
            title = "Must include",
            hint = "e.g. chicken, garlic",
            ingredients = filters.includeIngredients,
            onAdd = { onFiltersChange(filters.copy(includeIngredients = filters.includeIngredients + it)) },
            onRemove = { onFiltersChange(filters.copy(includeIngredients = filters.includeIngredients - it)) },
        )

        // Exclude ingredients
        IngredientFilter(
            title = "Must not include",
            hint = "e.g. peanuts, beef",
            ingredients = filters.excludeIngredients,
            onAdd = { onFiltersChange(filters.copy(excludeIngredients = filters.excludeIngredients + it)) },
            onRemove = { onFiltersChange(filters.copy(excludeIngredients = filters.excludeIngredients - it)) },
        )
    }
}

@Composable
private fun ServingsFilter(
    filters: RecipeFilters,
    onFiltersChange: (RecipeFilters) -> Unit,
) {
    val start = filters.minServings?.toFloat() ?: SERVINGS_MIN
    val end = filters.maxServings?.toFloat() ?: SERVINGS_MAX
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("Servings", style = MaterialTheme.typography.bodyLarge)
            Text(
                "${start.roundToInt()} - ${end.roundToInt()}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        RangeSlider(
            value = start..end,
            onValueChange = { range ->
                val newMin = range.start.roundToInt()
                val newMax = range.endInclusive.roundToInt()
                onFiltersChange(
                    filters.copy(
                        minServings = newMin.takeIf { it > SERVINGS_MIN.toInt() },
                        maxServings = newMax.takeIf { it < SERVINGS_MAX.toInt() },
                    ),
                )
            },
            valueRange = SERVINGS_MIN..SERVINGS_MAX,
            steps = (SERVINGS_MAX - SERVINGS_MIN).toInt() - 1,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun IngredientFilter(
    title: String,
    hint: String,
    ingredients: List<String>,
    onAdd: (String) -> Unit,
    onRemove: (String) -> Unit,
) {
    var text by remember { mutableStateOf("") }

    fun commit() {
        val value = text.trim()
        if (value.isNotEmpty() && ingredients.none { it.equals(value, ignoreCase = true) }) {
            onAdd(value)
        }
        text = ""
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, style = MaterialTheme.typography.bodyLarge)
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text(hint) },
            trailingIcon = {
                IconButton(onClick = { commit() }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add ingredient")
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { commit() }),
        )
        if (ingredients.isNotEmpty()) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ingredients.forEach { ingredient ->
                    InputChip(
                        selected = true,
                        onClick = { onRemove(ingredient) },
                        label = { Text(ingredient) },
                        trailingIcon = {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = "Remove $ingredient",
                            )
                        },
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
                minServings = 2,
                includeIngredients = listOf("garlic"),
                excludeIngredients = listOf("beef"),
            ),
            onFiltersChange = {},
            onClearAll = {},
        )
    }
}
