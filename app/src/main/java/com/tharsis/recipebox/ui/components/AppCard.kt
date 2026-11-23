package com.tharsis.recipebox.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tharsis.recipebox.ui.theme.RecipeBoxTheme
import com.tharsis.recipebox.ui.theme.Spacing

/**
 * Generic titled body card. Colors come from [MaterialTheme.colorScheme]
 * so light and dark themes stay readable.
 */
@Composable
fun AppCard(title: String, body: String, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(Spacing.cardRadius)
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(width = 1.5.dp, color = colors.primary, shape = shape),
        shape = shape,
        color = colors.surfaceVariant,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = Spacing.lg,
                vertical = Spacing.md + Spacing.xxs,
            ),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = colors.onSurface,
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurfaceVariant,
                modifier = Modifier.padding(top = Spacing.xs),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AppCardPreview() {
    RecipeBoxTheme {
        AppCard(
            title = "Step 1",
            body = "Lorem ipsum dolor sit amet consectetur. Elit pretium cras mattis.",
            modifier = Modifier.padding(Spacing.lg),
        )
    }
}
