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
import com.tharsis.recipebox.ui.theme.BrandGreen
import com.tharsis.recipebox.ui.theme.CardSurface
import com.tharsis.recipebox.ui.theme.RecipeBoxTheme
import com.tharsis.recipebox.ui.theme.TextPrimary
import com.tharsis.recipebox.ui.theme.TextSecondary

private val CardShape = RoundedCornerShape(14.dp)

/**
 * Shared step/ingredient card: soft gray fill + thin green border so the list
 * feels on-brand without looking as flat as a plain gray block.
 */
@Composable
fun ContentCard(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(width = 1.5.dp, color = BrandGreen, shape = CardShape),
        shape = CardShape,
        color = CardSurface,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(top = 6.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ContentCardPreview() {
    RecipeBoxTheme {
        ContentCard(
            title = "Step 1",
            body = "Lorem ipsum dolor sit amet consectetur. Elit pretium cras mattis.",
            modifier = Modifier.padding(16.dp),
        )
    }
}
