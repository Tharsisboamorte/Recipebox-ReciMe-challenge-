package com.tharsis.recipebox.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tharsis.recipebox.ui.theme.BrandGreen
import com.tharsis.recipebox.ui.theme.RecipeBoxTheme
import com.tharsis.recipebox.ui.theme.TextOnGreen
import com.tharsis.recipebox.ui.theme.TextSecondary

@Composable
fun RecipeSearchBar(
    searchText: String,
    activeFilterCount: Int,
    layoutMode: RecipeLayoutMode,
    onSearchTextChange: (String) -> Unit,
    onClearText: () -> Unit,
    onFilterClick: () -> Unit,
    onToggleLayout: () -> Unit,
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
            shape = RoundedCornerShape(14.dp),
            placeholder = { Text("Search recipe", color = TextSecondary) },
            leadingIcon = {
                Icon(Icons.Filled.Search, contentDescription = null, tint = TextSecondary)
            },
            trailingIcon = {
                if (searchText.isNotEmpty()) {
                    IconButton(onClick = onClearText) {
                        Icon(Icons.Filled.Close, contentDescription = "Clear search")
                    }
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BrandGreen,
                unfocusedBorderColor = TextSecondary.copy(alpha = 0.35f),
                cursorColor = BrandGreen,
            ),
        )

        IconButton(
            onClick = onToggleLayout,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = BrandGreen.copy(alpha = 0.12f),
                contentColor = BrandGreen,
            ),
            modifier = Modifier.size(48.dp),
        ) {
            Icon(
                imageVector = if (layoutMode == RecipeLayoutMode.Grid) {
                    Icons.Filled.ViewAgenda
                } else {
                    Icons.Filled.GridView
                },
                contentDescription = if (layoutMode == RecipeLayoutMode.Grid) {
                    "Switch to list"
                } else {
                    "Switch to grid"
                },
            )
        }

        Box {
            IconButton(
                onClick = onFilterClick,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = BrandGreen,
                    contentColor = TextOnGreen,
                ),
                modifier = Modifier.size(48.dp),
            ) {
                Icon(Icons.Filled.Tune, contentDescription = "Filters")
            }
            if (activeFilterCount > 0) {
                Badge(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-2).dp, y = 2.dp),
                    containerColor = MaterialTheme.colorScheme.error,
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
            layoutMode = RecipeLayoutMode.Grid,
            onSearchTextChange = {},
            onClearText = {},
            onFilterClick = {},
            onToggleLayout = {},
        )
    }
}
