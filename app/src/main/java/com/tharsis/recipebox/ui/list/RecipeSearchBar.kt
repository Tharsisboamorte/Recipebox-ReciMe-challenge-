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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tharsis.recipebox.R
import com.tharsis.recipebox.ui.theme.RecipeBoxTheme
import com.tharsis.recipebox.ui.theme.Spacing

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
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = modifier.padding(horizontal = Spacing.lg, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        OutlinedTextField(
            value = searchText,
            onValueChange = onSearchTextChange,
            modifier = Modifier.weight(1f),
            singleLine = true,
            shape = RoundedCornerShape(Spacing.cardRadius),
            placeholder = {
                Text(stringResource(R.string.search_hint), color = colors.onSurfaceVariant)
            },
            leadingIcon = {
                Icon(Icons.Filled.Search, contentDescription = null, tint = colors.onSurfaceVariant)
            },
            trailingIcon = {
                if (searchText.isNotEmpty()) {
                    IconButton(onClick = onClearText) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = stringResource(R.string.cd_clear_search),
                        )
                    }
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colors.primary,
                unfocusedBorderColor = colors.onSurfaceVariant.copy(alpha = 0.35f),
                cursorColor = colors.primary,
                focusedTextColor = colors.onSurface,
                unfocusedTextColor = colors.onSurface,
            ),
        )

        IconButton(
            onClick = onToggleLayout,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = colors.primary.copy(alpha = 0.12f),
                contentColor = colors.primary,
            ),
            modifier = Modifier.size(Spacing.iconButton),
        ) {
            Icon(
                imageVector = if (layoutMode == RecipeLayoutMode.Grid) {
                    Icons.Filled.ViewAgenda
                } else {
                    Icons.Filled.GridView
                },
                contentDescription = stringResource(
                    if (layoutMode == RecipeLayoutMode.Grid) {
                        R.string.cd_switch_to_list
                    } else {
                        R.string.cd_switch_to_grid
                    },
                ),
            )
        }

        Box {
            IconButton(
                onClick = onFilterClick,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = colors.primary,
                    contentColor = colors.onPrimary,
                ),
                modifier = Modifier.size(Spacing.iconButton),
            ) {
                Icon(
                    Icons.Filled.Tune,
                    contentDescription = stringResource(R.string.cd_filters),
                )
            }
            if (activeFilterCount > 0) {
                Badge(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-2).dp, y = 2.dp),
                    containerColor = colors.error,
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
