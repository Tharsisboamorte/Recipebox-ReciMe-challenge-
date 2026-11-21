package com.tharsis.recipebox.ui.list

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tharsis.recipebox.data.model.Recipe
import com.tharsis.recipebox.ui.components.RecipeImage
import com.tharsis.recipebox.ui.components.RecipeImageSize
import com.tharsis.recipebox.ui.components.RecipeSharedKeys
import com.tharsis.recipebox.ui.components.sharedElementModifier
import com.tharsis.recipebox.ui.preview.SampleRecipes
import com.tharsis.recipebox.ui.theme.BrandGreen
import com.tharsis.recipebox.ui.theme.OverlayScrim
import com.tharsis.recipebox.ui.theme.RecipeBoxTheme
import com.tharsis.recipebox.ui.theme.TextOnGreen
import com.tharsis.recipebox.ui.theme.TextSecondary

private val GridShape = RoundedCornerShape(16.dp)
private val ListShape = RoundedCornerShape(16.dp)

@Composable
fun RecipeCard(
    recipe: Recipe,
    onClick: () -> Unit,
    layoutMode: RecipeLayoutMode,
    modifier: Modifier = Modifier,
    sharedScope: SharedTransitionScope? = null,
    animatedScope: AnimatedVisibilityScope? = null,
) {

    when (layoutMode) {
        RecipeLayoutMode.Grid -> RecipeGridCard(
            recipe = recipe,
            onClick = onClick,
            modifier = modifier,
            sharedScope = sharedScope,
            animatedScope = animatedScope,
        )
        RecipeLayoutMode.List -> RecipeListCard(
            recipe = recipe,
            onClick = onClick,
            modifier = modifier,
            sharedScope = sharedScope,
            animatedScope = animatedScope,
        )
    }
}

@Composable
private fun RecipeGridCard(
    recipe: Recipe,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    sharedScope: SharedTransitionScope? = null,
    animatedScope: AnimatedVisibilityScope? = null,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(3f / 4f)
            .clip(GridShape)
            .clickable(onClick = onClick),
    ) {
        RecipeImage(
            imageUrl = recipe.imageUrl,
            contentDescription = recipe.title,
            size = RecipeImageSize.Thumbnail,
            modifier = Modifier
                .fillMaxSize()
                .sharedElementModifier(
                    sharedScope = sharedScope,
                    animatedScope = animatedScope,
                    key = RecipeSharedKeys.image(recipe.id),
                ),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .height(110.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, OverlayScrim),
                    ),
                ),
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp),
        ) {
            Text(
                text = recipe.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = TextOnGreen,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.sharedElementModifier(
                    sharedScope = sharedScope,
                    animatedScope = animatedScope,
                    key = RecipeSharedKeys.title(recipe.id),
                ),
            )
            Text(
                text = subtitleFor(recipe),
                style = MaterialTheme.typography.labelSmall,
                color = TextOnGreen.copy(alpha = 0.85f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@Composable
private fun RecipeListCard(
    recipe: Recipe,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    sharedScope: SharedTransitionScope? = null,
    animatedScope: AnimatedVisibilityScope? = null,
) {
    Surface(
        onClick = onClick,
        shape = ListShape,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RecipeImage(
                imageUrl = recipe.imageUrl,
                contentDescription = recipe.title,
                size = RecipeImageSize.Thumbnail,
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .sharedElementModifier(
                        sharedScope = sharedScope,
                        animatedScope = animatedScope,
                        key = RecipeSharedKeys.image(recipe.id),
                    ),
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.sharedElementModifier(
                        sharedScope = sharedScope,
                        animatedScope = animatedScope,
                        key = RecipeSharedKeys.title(recipe.id),
                    ),
                )
                Text(
                    text = recipe.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp),
                )
                Text(
                    text = subtitleFor(recipe),
                    style = MaterialTheme.typography.labelMedium,
                    color = BrandGreen,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}

private fun subtitleFor(recipe: Recipe): String {
    val diet = recipe.dietary.activeLabels.firstOrNull()
    return buildString {
        append("${recipe.servings} servings")
        if (diet != null) {
            append(" · ")
            append(diet)
        }
    }
}

@Preview(showBackground = true, widthDp = 180)
@Composable
private fun RecipeGridCardPreview() {
    RecipeBoxTheme {
        RecipeCard(
            recipe = SampleRecipes.margherita,
            onClick = {},
            layoutMode = RecipeLayoutMode.Grid,
            modifier = Modifier.padding(12.dp),
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun RecipeListCardPreview() {
    RecipeBoxTheme {
        RecipeCard(
            recipe = SampleRecipes.margherita,
            onClick = {},
            layoutMode = RecipeLayoutMode.List,
            modifier = Modifier.padding(12.dp),
        )
    }
}
