package com.tharsis.recipebox.ui.list

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.tharsis.recipebox.R
import com.tharsis.recipebox.domain.model.Recipe
import com.tharsis.recipebox.ui.components.DietaryChipsRow
import com.tharsis.recipebox.ui.components.RecipeImage
import com.tharsis.recipebox.ui.components.RecipeImageSize
import com.tharsis.recipebox.ui.components.RecipeSharedKeys
import com.tharsis.recipebox.ui.components.sharedElementModifier
import com.tharsis.recipebox.ui.components.toDietaryTags
import com.tharsis.recipebox.ui.preview.SampleRecipes
import com.tharsis.recipebox.ui.theme.OverlayScrim
import com.tharsis.recipebox.ui.theme.RecipeBoxTheme
import com.tharsis.recipebox.ui.theme.Spacing

private val GridShape = RoundedCornerShape(Spacing.imageRadius)
private val ListCardShape = RoundedCornerShape(20.dp)
private val ListThumbShape = RoundedCornerShape(16.dp)
private val ListCardHeight = 140.dp
private val ListThumbSize = 108.dp

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
            imageUri = recipe.imageUri,
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
                .padding(Spacing.md),
        ) {
            Text(
                text = recipe.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.sharedElementModifier(
                    sharedScope = sharedScope,
                    animatedScope = animatedScope,
                    key = RecipeSharedKeys.title(recipe.id),
                ),
            )
            Text(
                text = pluralStringResource(
                    R.plurals.servings_label,
                    recipe.servings,
                    recipe.servings,
                ),
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.85f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = Spacing.xxs),
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
    val colors = MaterialTheme.colorScheme
    val openLabel = stringResource(R.string.cd_open_recipe)
    val dietaryTags = remember(recipe.dietary) { recipe.dietary.toDietaryTags() }

    Surface(
        shape = ListCardShape,
        color = colors.surface,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        modifier = modifier
            .fillMaxWidth()
            .height(ListCardHeight)
            .border(
                width = 1.dp,
                color = colors.outlineVariant,
                shape = ListCardShape,
            )
            .semantics(mergeDescendants = true) {
                onClick(label = openLabel, action = null)
            }
            .clickable(
                onClickLabel = openLabel,
                onClick = onClick,
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.lg),
            verticalAlignment = Alignment.Top,
        ) {
            RecipeImage(
                imageUri = recipe.imageUri,
                contentDescription = recipe.title,
                size = RecipeImageSize.Thumbnail,
                modifier = Modifier
                    .size(ListThumbSize)
                    .clip(ListThumbShape)
                    .sharedElementModifier(
                        sharedScope = sharedScope,
                        animatedScope = animatedScope,
                        key = RecipeSharedKeys.image(recipe.id),
                    ),
            )
            Spacer(Modifier.width(Spacing.md))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(
                        text = recipe.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.onSurface,
                        maxLines = 1,
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
                        color = colors.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = Spacing.xxs),
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.People,
                        contentDescription = null,
                        tint = colors.onSurfaceVariant,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = pluralStringResource(
                            R.plurals.servings_label,
                            recipe.servings,
                            recipe.servings,
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.onSurfaceVariant,
                        maxLines = 1,
                    )
                    Spacer(Modifier.weight(1f))
                    DietaryChipsRow(tags = dietaryTags)
                }
            }
        }
    }
}

private class ListCardPreviewProvider : PreviewParameterProvider<Recipe> {
    override val values = sequenceOf(
        SampleRecipes.margherita, // short title, 1 tag
        SampleRecipes.curry, // long title, 2 tags
        SampleRecipes.chickpeaStew, // short title, 3 tags → 2 + "+1"
        SampleRecipes.buddhaBowl, // 4 tags → 2 + "+2"
    )
}

@Preview(showBackground = true, widthDp = 180)
@Composable
private fun RecipeGridCardPreview() {
    RecipeBoxTheme {
        RecipeCard(
            recipe = SampleRecipes.margherita,
            onClick = {},
            layoutMode = RecipeLayoutMode.Grid,
            modifier = Modifier.padding(Spacing.md),
        )
    }
}

@Preview(showBackground = true, widthDp = 360, name = "List — fixed height")
@Composable
private fun RecipeListCardPreview(
    @PreviewParameter(ListCardPreviewProvider::class) recipe: Recipe,
) {
    RecipeBoxTheme {
        RecipeCard(
            recipe = recipe,
            onClick = {},
            layoutMode = RecipeLayoutMode.List,
            modifier = Modifier.padding(Spacing.md),
        )
    }
}
