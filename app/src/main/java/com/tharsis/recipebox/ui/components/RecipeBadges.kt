package com.tharsis.recipebox.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.Grain
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tharsis.recipebox.R
import com.tharsis.recipebox.domain.model.DietaryAttributes
import com.tharsis.recipebox.ui.theme.Spacing

/** Dietary tags in display-priority order (Vegan before Vegetarian). */
enum class DietaryTag {
    Vegan,
    Vegetarian,
    GlutenFree,
    DairyFree,
}

/**
 * Maps dietary flags to ordered tags. Vegan implies vegetarian, so Vegetarian
 * is omitted when Vegan is set. Order: Vegan > Vegetarian > Gluten-Free > Dairy-Free.
 */
fun DietaryAttributes.toDietaryTags(): List<DietaryTag> = buildList {
    when {
        vegan -> add(DietaryTag.Vegan)
        vegetarian -> add(DietaryTag.Vegetarian)
    }
    if (glutenFree) add(DietaryTag.GlutenFree)
    if (dairyFree) add(DietaryTag.DairyFree)
}

/**
 * Full-size tonal chips for detail (may wrap). Prefer [DietaryChipsRow] on list cards.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DietaryChips(
    dietary: DietaryAttributes,
    modifier: Modifier = Modifier,
) {
    val tags = remember(dietary) { dietary.toDietaryTags() }
    if (tags.isEmpty()) return

    val colors = MaterialTheme.colorScheme
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
    ) {
        tags.forEach { tag ->
            AssistChip(
                onClick = {},
                enabled = false,
                label = {
                    Text(
                        text = tag.label(),
                        style = MaterialTheme.typography.labelMedium,
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = tag.icon,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                },
                shape = RoundedCornerShape(50),
                border = AssistChipDefaults.assistChipBorder(
                    enabled = false,
                    borderColor = colors.outline.copy(alpha = 0.35f),
                    disabledBorderColor = colors.outline.copy(alpha = 0.35f),
                ),
                colors = AssistChipDefaults.assistChipColors(
                    disabledContainerColor = colors.primaryContainer,
                    disabledLabelColor = colors.onPrimaryContainer,
                    disabledLeadingIconContentColor = colors.onPrimaryContainer,
                ),
            )
        }
    }
}

/**
 * Single-line compact diet chips for list cards: at most 2 tags, then "+N".
 * Never wraps — keeps fixed-height cards stable.
 */
@Composable
fun DietaryChipsRow(
    tags: List<DietaryTag>,
    modifier: Modifier = Modifier,
    maxVisible: Int = 2,
) {
    if (tags.isEmpty()) return

    val visible = tags.take(maxVisible)
    val overflow = tags.size - visible.size

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xxs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        visible.forEach { tag ->
            CompactDietaryChip(label = tag.label(), icon = tag.icon)
        }
        if (overflow > 0) {
            CompactDietaryChip(
                label = stringResource(R.string.dietary_more_count, overflow),
                icon = null,
            )
        }
    }
}

@Composable
private fun CompactDietaryChip(
    label: String,
    icon: ImageVector?,
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(50)
    Surface(
        shape = shape,
        color = colors.primaryContainer,
        modifier = Modifier
            .height(24.dp)
            .border(
                width = 1.dp,
                color = colors.outline.copy(alpha = 0.35f),
                shape = shape,
            ),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = colors.onPrimaryContainer,
                    modifier = Modifier.size(14.dp),
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = colors.onPrimaryContainer,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun DietaryTag.label(): String = stringResource(
    when (this) {
        DietaryTag.Vegan -> R.string.dietary_vegan
        DietaryTag.Vegetarian -> R.string.dietary_vegetarian
        DietaryTag.GlutenFree -> R.string.dietary_gluten_free
        DietaryTag.DairyFree -> R.string.dietary_dairy_free
    },
)

private val DietaryTag.icon: ImageVector
    get() = when (this) {
        DietaryTag.Vegan -> Icons.Outlined.Spa
        DietaryTag.Vegetarian -> Icons.Outlined.Eco
        DietaryTag.GlutenFree -> Icons.Outlined.Grain
        DietaryTag.DairyFree -> Icons.Outlined.WaterDrop
    }
