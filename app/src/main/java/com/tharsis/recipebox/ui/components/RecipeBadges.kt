package com.tharsis.recipebox.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.tharsis.recipebox.R
import com.tharsis.recipebox.domain.model.DietaryAttributes
import com.tharsis.recipebox.ui.theme.Spacing

/** Row of chips describing the dietary attributes a recipe satisfies. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DietaryChips(
    dietary: DietaryAttributes,
    modifier: Modifier = Modifier,
) {
    val labels = buildList {
        if (dietary.vegetarian) add(stringResource(R.string.dietary_vegetarian))
        if (dietary.vegan) add(stringResource(R.string.dietary_vegan))
        if (dietary.glutenFree) add(stringResource(R.string.dietary_gluten_free))
        if (dietary.dairyFree) add(stringResource(R.string.dietary_dairy_free))
    }
    if (labels.isEmpty()) return

    val colors = MaterialTheme.colorScheme
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
    ) {
        labels.forEach { label ->
            SuggestionChip(
                onClick = {},
                enabled = false,
                label = { Text(label) },
                shape = RoundedCornerShape(50),
                colors = SuggestionChipDefaults.suggestionChipColors(
                    disabledContainerColor = colors.primaryContainer,
                    disabledLabelColor = colors.onPrimaryContainer,
                ),
            )
        }
    }
}
