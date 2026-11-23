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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.tharsis.recipebox.R
import com.tharsis.recipebox.domain.model.Recipe
import com.tharsis.recipebox.ui.components.AppCard
import com.tharsis.recipebox.ui.components.DietaryChips
import com.tharsis.recipebox.ui.components.ErrorState
import com.tharsis.recipebox.ui.components.LoadingState
import com.tharsis.recipebox.ui.components.RecipeImage
import com.tharsis.recipebox.ui.components.RecipeImageSize
import com.tharsis.recipebox.ui.components.RecipeSharedKeys
import com.tharsis.recipebox.ui.components.sharedElementModifier
import com.tharsis.recipebox.ui.preview.SampleRecipes
import com.tharsis.recipebox.ui.theme.RecipeBoxTheme
import com.tharsis.recipebox.ui.theme.Spacing
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

    LaunchedEffect(viewModel) {
        viewModel.sideEffects.collect { effect ->
            when (effect) {
                RecipeDetailSideEffect.NavigateBack -> onBack()
            }
        }
    }

    RecipeDetailScreen(
        state = uiState,
        onIntent = viewModel::onIntent,
        modifier = modifier,
        sharedScope = sharedScope,
        animatedScope = animatedScope,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    state: RecipeDetailUiState,
    onIntent: (RecipeDetailIntent) -> Unit,
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
                    IconButton(onClick = { onIntent(RecipeDetailIntent.Back) }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground,
                ),
            )
        },
    ) { innerPadding ->
        when {
            state.isLoading -> LoadingState(Modifier.padding(innerPadding))
            state.recipe != null -> RecipeDetailContent(
                recipe = state.recipe,
                contentPadding = innerPadding,
                sharedScope = sharedScope,
                animatedScope = animatedScope,
            )
            else -> ErrorState(
                message = state.errorMessage
                    ?: stringResource(R.string.detail_not_found),
                onRetry = { onIntent(RecipeDetailIntent.Retry) },
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
            .padding(horizontal = Spacing.lg)
            .padding(bottom = Spacing.xxl),
    ) {
        RecipeImage(
            imageUrl = recipe.imageUrl,
            contentDescription = recipe.title,
            size = RecipeImageSize.Hero,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 10f)
                .clip(RoundedCornerShape(Spacing.imageRadius))
                .sharedElementModifier(
                    sharedScope = sharedScope,
                    animatedScope = animatedScope,
                    key = RecipeSharedKeys.image(recipe.id),
                ),
        )

        Spacer(modifier = Modifier.height(Spacing.lg))

        Text(
            text = recipe.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.sharedElementModifier(
                sharedScope = sharedScope,
                animatedScope = animatedScope,
                key = RecipeSharedKeys.title(recipe.id),
            ),
        )

        Text(
            text = recipe.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = Spacing.sm),
        )

        DietaryChips(
            dietary = recipe.dietary,
            modifier = Modifier.padding(top = Spacing.md),
        )

        Spacer(modifier = Modifier.height(Spacing.xl))

        DetailTabRow(
            selected = selectedTab,
            onSelect = { selectedTab = it },
        )

        Spacer(modifier = Modifier.height(Spacing.lg))

        MetaRow(
            servings = recipe.servings,
            trailingLabel = when (selectedTab) {
                DetailTab.Ingredients -> stringResource(
                    R.string.detail_items_count,
                    recipe.ingredients.size,
                )
                DetailTab.Procedure -> stringResource(
                    R.string.detail_steps_count,
                    recipe.instructions.size,
                )
            },
        )

        Spacer(modifier = Modifier.height(Spacing.md))

        when (selectedTab) {
            DetailTab.Ingredients -> {
                recipe.ingredients.forEachIndexed { index, ingredient ->
                    AppCard(
                        title = stringResource(R.string.detail_ingredient_n, index + 1),
                        body = ingredient,
                        modifier = Modifier.padding(bottom = Spacing.md),
                    )
                }
            }
            DetailTab.Procedure -> {
                recipe.instructions.forEachIndexed { index, step ->
                    AppCard(
                        title = stringResource(R.string.detail_step_n, index + 1),
                        body = step,
                        modifier = Modifier.padding(bottom = Spacing.md),
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailTabRow(selected: DetailTab, onSelect: (DetailTab) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        DetailTabButton(
            label = stringResource(R.string.detail_tab_ingredient),
            selected = selected == DetailTab.Ingredients,
            onClick = { onSelect(DetailTab.Ingredients) },
            modifier = Modifier.weight(1f),
        )
        DetailTabButton(
            label = stringResource(R.string.detail_tab_procedure),
            selected = selected == DetailTab.Procedure,
            onClick = { onSelect(DetailTab.Procedure) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun DetailTabButton(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(Spacing.chipRadius)
    Box(
        modifier = modifier
            .height(Spacing.buttonHeight)
            .clip(shape)
            .background(if (selected) colors.primary else colors.surfaceVariant)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = if (selected) colors.onPrimary else colors.primary,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun MetaRow(servings: Int, trailingLabel: String) {
    val muted = MaterialTheme.colorScheme.onSurfaceVariant
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.Restaurant,
                contentDescription = null,
                tint = muted,
            )
            Text(
                text = "  " + pluralStringResource(R.plurals.servings_label, servings, servings),
                style = MaterialTheme.typography.bodySmall,
                color = muted,
            )
        }
        Text(
            text = trailingLabel,
            style = MaterialTheme.typography.bodySmall,
            color = muted,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RecipeDetailScreenPreview() {
    RecipeBoxTheme {
        RecipeDetailScreen(
            state = RecipeDetailUiState(isLoading = false, recipe = SampleRecipes.margherita),
            onIntent = {},
        )
    }
}
