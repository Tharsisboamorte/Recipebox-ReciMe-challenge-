package com.tharsis.recipebox.ui.detail

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tharsis.recipebox.data.model.Recipe
import com.tharsis.recipebox.ui.components.ContentCard
import com.tharsis.recipebox.ui.components.DietaryChips
import com.tharsis.recipebox.ui.components.ErrorState
import com.tharsis.recipebox.ui.components.LoadingState
import com.tharsis.recipebox.ui.components.RecipeImage
import com.tharsis.recipebox.ui.components.RecipeImageSize
import com.tharsis.recipebox.ui.components.RecipeSharedKeys
import com.tharsis.recipebox.ui.components.sharedElementModifier
import com.tharsis.recipebox.ui.preview.SampleRecipes
import com.tharsis.recipebox.ui.theme.BrandGreen
import com.tharsis.recipebox.ui.theme.RecipeBoxTheme
import com.tharsis.recipebox.ui.theme.TextOnGreen
import com.tharsis.recipebox.ui.theme.TextPrimary
import com.tharsis.recipebox.ui.theme.TextSecondary
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

private enum class DetailTab { Ingredients, Procedure }

@Composable
fun RecipeDetailRoute(
    recipeId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    sharedScope: SharedTransitionScope? = null,
    animatedScope: AnimatedVisibilityScope? = null,
    viewModel: RecipeDetailViewModel = koinViewModel { parametersOf(recipeId) },
) {
    val uiState by viewModel.uiState.collectAsState()
    RecipeDetailScreen(
        uiState = uiState,
        onBack = onBack,
        onRetry = viewModel::load,
        modifier = modifier,
        sharedScope = sharedScope,
        animatedScope = animatedScope,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    uiState: RecipeDetailUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    sharedScope: SharedTransitionScope? = null,
    animatedScope: AnimatedVisibilityScope? = null,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
    ) { innerPadding ->
        when {
            uiState.isLoading -> LoadingState(Modifier.padding(innerPadding))
            uiState.recipe != null -> RecipeDetailContent(
                recipe = uiState.recipe,
                contentPadding = innerPadding,
                sharedScope = sharedScope,
                animatedScope = animatedScope,
            )
            else -> ErrorState(
                message = uiState.errorMessage ?: "Recipe not found.",
                onRetry = onRetry,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

@Composable
private fun RecipeDetailContent(
    recipe: Recipe,
    contentPadding: PaddingValues,
    sharedScope: SharedTransitionScope?,
    animatedScope: AnimatedVisibilityScope?,
) {
    var selectedTab by remember { mutableStateOf(DetailTab.Ingredients) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = contentPadding.calculateTopPadding())
            .padding(horizontal = 16.dp)
            .padding(bottom = 24.dp),
    ) {
        RecipeImage(
            imageUrl = recipe.imageUrl,
            contentDescription = recipe.title,
            size = RecipeImageSize.Hero,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 10f)
                .clip(RoundedCornerShape(16.dp))
                .sharedElementModifier(
                    sharedScope = sharedScope,
                    animatedScope = animatedScope,
                    key = RecipeSharedKeys.image(recipe.id),
                ),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = recipe.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.sharedElementModifier(
                sharedScope = sharedScope,
                animatedScope = animatedScope,
                key = RecipeSharedKeys.title(recipe.id),
            ),
        )

        Text(
            text = recipe.description,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.padding(top = 8.dp),
        )

        DietaryChips(
            dietary = recipe.dietary,
            modifier = Modifier.padding(top = 12.dp),
        )

        Spacer(modifier = Modifier.height(20.dp))

        DetailTabRow(
            selected = selectedTab,
            onSelect = { selectedTab = it },
        )

        Spacer(modifier = Modifier.height(16.dp))

        MetaRow(
            servings = recipe.servings,
            trailingLabel = when (selectedTab) {
                DetailTab.Ingredients -> "${recipe.ingredients.size} Items"
                DetailTab.Procedure -> "${recipe.instructions.size} Steps"
            },
        )

        Spacer(modifier = Modifier.height(12.dp))

        when (selectedTab) {
            DetailTab.Ingredients -> {
                recipe.ingredients.forEachIndexed { index, ingredient ->
                    ContentCard(
                        title = "Ingredient ${index + 1}",
                        body = ingredient,
                        modifier = Modifier.padding(bottom = 12.dp),
                    )
                }
            }
            DetailTab.Procedure -> {
                recipe.instructions.forEachIndexed { index, step ->
                    ContentCard(
                        title = "Step ${index + 1}",
                        body = step,
                        modifier = Modifier.padding(bottom = 12.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailTabRow(
    selected: DetailTab,
    onSelect: (DetailTab) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        DetailTabButton(
            label = "Ingredient",
            selected = selected == DetailTab.Ingredients,
            onClick = { onSelect(DetailTab.Ingredients) },
            modifier = Modifier.weight(1f),
        )
        DetailTabButton(
            label = "Procedure",
            selected = selected == DetailTab.Procedure,
            onClick = { onSelect(DetailTab.Procedure) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun DetailTabButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier = modifier
            .height(44.dp)
            .clip(shape)
            .background(if (selected) BrandGreen else MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = if (selected) TextOnGreen else BrandGreen,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun MetaRow(
    servings: Int,
    trailingLabel: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.Restaurant,
                contentDescription = null,
                tint = TextSecondary,
            )
            Text(
                text = "  $servings serve${if (servings == 1) "" else "s"}",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
            )
        }
        Text(
            text = trailingLabel,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RecipeDetailScreenPreview() {
    RecipeBoxTheme {
        RecipeDetailScreen(
            uiState = RecipeDetailUiState(isLoading = false, recipe = SampleRecipes.margherita),
            onBack = {},
            onRetry = {},
        )
    }
}
