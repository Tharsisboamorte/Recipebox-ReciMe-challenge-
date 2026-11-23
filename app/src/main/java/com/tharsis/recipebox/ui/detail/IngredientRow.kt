package com.tharsis.recipebox.ui.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tharsis.recipebox.ui.theme.RecipeBoxTheme
import com.tharsis.recipebox.ui.theme.Spacing

/**
 * Compact ingredient row (~56.dp). Name is primary; leading quantity (when
 * parsed) sits right-aligned as secondary scan text.
 */
@Composable
fun IngredientRow(
    ingredient: String,
    modifier: Modifier = Modifier,
    showDivider: Boolean = true,
) {
    val parsed = remember(ingredient) { parseIngredient(ingredient) }
    val colors = MaterialTheme.colorScheme

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = parsed.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = colors.onSurface,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = Spacing.md),
            )
            if (parsed.quantity != null) {
                Text(
                    text = parsed.quantity,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant,
                    textAlign = TextAlign.End,
                )
            }
        }
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = Spacing.lg),
                color = colors.outlineVariant,
            )
        }
    }
}

internal data class ParsedIngredient(
    val name: String,
    val quantity: String?,
)

/**
 * Splits strings like "250g pizza dough" / "2 tbsp olive oil" into quantity + name.
 * Unmatched lines (e.g. "Fresh basil leaves") keep the full text as [ParsedIngredient.name].
 */
internal fun parseIngredient(raw: String): ParsedIngredient {
    val trimmed = raw.trim()
    if (trimmed.isEmpty()) return ParsedIngredient(name = raw, quantity = null)

    val match = QUANTITY_PREFIX.matchEntire(trimmed) ?: return ParsedIngredient(
        name = trimmed,
        quantity = null,
    )
    val quantity = match.groupValues[1].trim()
    val name = match.groupValues[2].trim()
    if (name.isEmpty()) return ParsedIngredient(name = trimmed, quantity = null)
    return ParsedIngredient(name = name, quantity = quantity)
}

private val QUANTITY_PREFIX = Regex(
    pattern = """^(\d+(?:[./]\d+)?\s*(?:g|kg|ml|l|tsp|tbsp|cups?|oz|lbs?|cloves?|cans?)?)\s+(.+)$""",
    option = RegexOption.IGNORE_CASE,
)

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun IngredientRowPreview() {
    RecipeBoxTheme {
        Column {
            IngredientRow(ingredient = "250g pizza dough")
            IngredientRow(ingredient = "2 tbsp olive oil")
            IngredientRow(ingredient = "Fresh basil leaves", showDivider = false)
        }
    }
}
