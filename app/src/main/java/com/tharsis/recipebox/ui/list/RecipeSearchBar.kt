package com.tharsis.recipebox.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tharsis.recipebox.ui.theme.RecipeBoxTheme

/**
 * Top search row: a free-text field (matches title, description and
 * instructions) plus a filter button that surfaces the active filter count.
 */
@Composable
fun RecipeSearchBar(
    searchText: String,
    activeFilterCount: Int,
    onSearchTextChange: (String) -> Unit,
    onClearText: () -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OutlinedTextField(
            value = searchText,
            onValueChange = onSearchTextChange,
            modifier = Modifier.weight(1f),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            placeholder = { Text("Search recipes & steps") },
            leadingIcon = {
                Icon(Icons.Filled.Search, contentDescription = null)
            },
            trailingIcon = {
                if (searchText.isNotEmpty()) {
                    IconButton(onClick = onClearText) {
                        Icon(Icons.Filled.Close, contentDescription = "Clear search")
                    }
                }
            },
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                imeAction = ImeAction.Search,
            ),
        )

        Box {
            FilledTonalIconButton(onClick = onFilterClick) {
                Icon(Icons.Filled.Tune, contentDescription = "Filters")
            }
            if (activeFilterCount > 0) {
                Badge(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-2).dp, y = 2.dp),
                ) {
                    Text(activeFilterCount.toString())
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RecipeSearchBarPreview() {
    RecipeBoxTheme {
        RecipeSearchBar(
            searchText = "curry",
            activeFilterCount = 2,
            onSearchTextChange = {},
            onClearText = {},
            onFilterClick = {},
        )
    }
}
