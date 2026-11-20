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
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tharsis.recipebox.ui.theme.BrandGreen
import com.tharsis.recipebox.ui.theme.BrandGreenSoft
import com.tharsis.recipebox.ui.theme.RecipeBoxTheme
import com.tharsis.recipebox.ui.theme.TextOnGreen
import com.tharsis.recipebox.ui.theme.TextPrimary
import com.tharsis.recipebox.ui.theme.TextSecondary

private val ChipShape = RoundedCornerShape(50)

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
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        FilterSheetContent(
            filters = filters,
            onFiltersChange = onFiltersChange,
            onClearAll = onClearAll,
            onApply = onDismiss,
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
            .padding(horizontal = 24.dp)
            .padding(bottom = 28.dp)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(22.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Filter Search",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.weight(1f),
            )
            if (filters.hasAny) {
                TextButton(onClick = onClearAll) {
                    Text("Clear", color = BrandGreen)
                }
            }
        }

        FilterSection(title = "Diet") {
            FilterChoiceChip(
                label = "All",
                selected = !filters.vegetarianOnly,
                onClick = { onFiltersChange(filters.copy(vegetarianOnly = false)) },
            )
            FilterChoiceChip(
                label = "Vegetarian",
                selected = filters.vegetarianOnly,
                onClick = { onFiltersChange(filters.copy(vegetarianOnly = true)) },
            )
        }

        FilterSection(title = "Servings") {
            ServingsChip(
                label = "Any",
                selected = filters.minServings == null && filters.maxServings == null,
                onClick = {
                    onFiltersChange(filters.copy(minServings = null, maxServings = null))
                },
            )
            ServingsChip(
                label = "1–2",
                selected = filters.minServings == 1 && filters.maxServings == 2,
                onClick = {
                    onFiltersChange(filters.copy(minServings = 1, maxServings = 2))
                },
            )
            ServingsChip(
                label = "3–4",
                selected = filters.minServings == 3 && filters.maxServings == 4,
                onClick = {
                    onFiltersChange(filters.copy(minServings = 3, maxServings = 4))
                },
            )
            ServingsChip(
                label = "5+",
                selected = filters.minServings == 5 && filters.maxServings == null,
                onClick = {
                    onFiltersChange(filters.copy(minServings = 5, maxServings = null))
                },
            )
        }

        IngredientChipEditor(
            title = "Must include",
            hint = "e.g. garlic, chicken",
            ingredients = filters.includeIngredients,
            onAdd = { onFiltersChange(filters.copy(includeIngredients = filters.includeIngredients + it)) },
            onRemove = { onFiltersChange(filters.copy(includeIngredients = filters.includeIngredients - it)) },
        )

        IngredientChipEditor(
            title = "Must not include",
            hint = "e.g. peanuts, beef",
            ingredients = filters.excludeIngredients,
            onAdd = { onFiltersChange(filters.copy(excludeIngredients = filters.excludeIngredients + it)) },
            onRemove = { onFiltersChange(filters.copy(excludeIngredients = filters.excludeIngredients - it)) },
        )

        Button(
            onClick = onApply,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandGreen,
                contentColor = TextOnGreen,
            ),
        ) {
            Text("Filter", fontWeight = FontWeight.SemiBold)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FilterSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
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
private fun FilterChoiceChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        shape = ChipShape,
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) BrandGreen else BrandGreenSoft,
        ),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.surface,
            labelColor = BrandGreen,
            selectedContainerColor = BrandGreen,
            selectedLabelColor = TextOnGreen,
        ),
    )
}

@Composable
private fun ServingsChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
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
    var text by remember { mutableStateOf("") }

    fun commit() {
        val value = text.trim()
        if (value.isNotEmpty() && ingredients.none { it.equals(value, ignoreCase = true) }) {
            onAdd(value)
        }
        text = ""
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
        )
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text(hint, color = TextSecondary) },
            trailingIcon = {
                IconButton(onClick = { commit() }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add ingredient", tint = BrandGreen)
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { commit() }),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BrandGreen,
                unfocusedBorderColor = BrandGreenSoft,
                cursorColor = BrandGreen,
            ),
        )
        if (ingredients.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ingredients.forEach { ingredient ->
                    InputChip(
                        selected = true,
                        onClick = { onRemove(ingredient) },
                        label = { Text(ingredient) },
                        trailingIcon = {
                            Icon(Icons.Filled.Close, contentDescription = "Remove $ingredient")
                        },
                        shape = ChipShape,
                        colors = InputChipDefaults.inputChipColors(
                            selectedContainerColor = BrandGreen,
                            selectedLabelColor = TextOnGreen,
                            selectedTrailingIconColor = TextOnGreen,
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
