package com.tharsis.recipebox.ui.detail

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tharsis.recipebox.R
import com.tharsis.recipebox.domain.model.Recipe
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

private enum class DetailTab { Ingredients, Instructions }

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
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { innerPadding ->
        when {
            state.isLoading -> LoadingState(
                Modifier
                    .padding(innerPadding)
                    .statusBarsPadding(),
            )
            state.recipe != null -> RecipeDetailContent(
                recipe = state.recipe,
                onBack = { onIntent(RecipeDetailIntent.Back) },
                sharedScope = sharedScope,
                animatedScope = animatedScope,
                modifier = Modifier.padding(innerPadding),
            )
            else -> ErrorState(
                message = state.errorMessage
                    ?: stringResource(R.string.detail_not_found),
                onRetry = { onIntent(RecipeDetailIntent.Retry) },
                modifier = Modifier
                    .padding(innerPadding)
                    .statusBarsPadding(),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipeDetailContent(
    recipe: Recipe,
    onBack: () -> Unit,
    sharedScope: SharedTransitionScope?,
    animatedScope: AnimatedVisibilityScope?,
    modifier: Modifier = Modifier,
) {
    var selectedTab by rememberSaveable { mutableStateOf(DetailTab.Ingredients) }
    val listState = rememberLazyListState()
    val navBottom = WindowInsets.navigationBars
        .asPaddingValues()
        .calculateBottomPadding()
    val colors = MaterialTheme.colorScheme

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = navBottom + Spacing.lg),
    ) {
        item(key = "hero") {
            HeroWithBack(
                recipe = recipe,
                onBack = onBack,
                sharedScope = sharedScope,
                animatedScope = animatedScope,
            )
        }

        item(key = "header") {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.lg)
                    .padding(top = Spacing.lg),
            ) {
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground,
                    modifier = Modifier.sharedElementModifier(
                        sharedScope = sharedScope,
                        animatedScope = animatedScope,
                        key = RecipeSharedKeys.title(recipe.id),
                    ),
                )
                Text(
                    text = recipe.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant,
                    modifier = Modifier.padding(top = Spacing.sm),
                )
                DietaryChips(
                    dietary = recipe.dietary,
                    modifier = Modifier.padding(top = Spacing.md),
                )
            }
        }

        stickyHeader(key = "tabs") {
            Surface(color = colors.background) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.lg)
                        .padding(top = Spacing.lg, bottom = Spacing.sm),
                ) {
                    DetailTabRow(
                        selected = selectedTab,
                        onSelect = { selectedTab = it },
                    )
                    MetaRow(
                        servings = recipe.servings,
                        trailingLabel = when (selectedTab) {
                            DetailTab.Ingredients -> stringResource(
                                R.string.detail_items_count,
                                recipe.ingredients.size,
                            )
                            DetailTab.Instructions -> stringResource(
                                R.string.detail_steps_count,
                                recipe.instructions.size,
                            )
                        },
                        modifier = Modifier.padding(top = Spacing.md),
                    )
                }
            }
        }

        when (selectedTab) {
            DetailTab.Ingredients -> {
                itemsIndexed(
                    items = recipe.ingredients,
                    key = { index, item -> "ing-$index-$item" },
                ) { index, ingredient ->
                    IngredientRow(
                        ingredient = ingredient,
                        showDivider = index < recipe.ingredients.lastIndex,
                    )
                }
            }
            DetailTab.Instructions -> {
                itemsIndexed(
                    items = recipe.instructions,
                    key = { index, item -> "step-$index-$item" },
                ) { index, step ->
                    InstructionRow(
                        stepNumber = index + 1,
                        text = step,
                        showDivider = index < recipe.instructions.lastIndex,
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroWithBack(
    recipe: Recipe,
    onBack: () -> Unit,
    sharedScope: SharedTransitionScope?,
    animatedScope: AnimatedVisibilityScope?,
) {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.lg)
            .padding(top = Spacing.sm),
    ) {
        RecipeImage(
            imageUri = recipe.imageUri,
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
        Surface(
            shape = CircleShape,
            color = colors.scrim.copy(alpha = 0.35f),
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(Spacing.sm)
                .size(48.dp),
        ) {
            IconButton(
                onClick = onBack,
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = colors.inverseOnSurface,
                ),
                modifier = Modifier.fillMaxSize(),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.cd_back),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailTabRow(
    selected: DetailTab,
    onSelect: (DetailTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tabs = DetailTab.entries
    SingleChoiceSegmentedButtonRow(modifier = modifier.fillMaxWidth()) {
        tabs.forEachIndexed { index, tab ->
            SegmentedButton(
                selected = selected == tab,
                onClick = { onSelect(tab) },
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = tabs.size,
                ),
            ) {
                Text(
                    text = stringResource(
                        when (tab) {
                            DetailTab.Ingredients -> R.string.detail_tab_ingredient
                            DetailTab.Instructions -> R.string.detail_tab_procedure
                        },
                    ),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

@Composable
private fun MetaRow(
    servings: Int,
    trailingLabel: String,
    modifier: Modifier = Modifier,
) {
    val muted = MaterialTheme.colorScheme.onSurfaceVariant
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.Restaurant,
                contentDescription = null,
                tint = muted,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(Spacing.xs))
            Text(
                text = pluralStringResource(R.plurals.servings_label, servings, servings),
                style = MaterialTheme.typography.bodyMedium,
                color = muted,
            )
        }
        Text(
            text = trailingLabel,
            style = MaterialTheme.typography.bodyMedium,
            color = muted,
        )
    }
}

@Composable
private fun InstructionRow(
    stepNumber: Int,
    text: String,
    modifier: Modifier = Modifier,
    showDivider: Boolean = true,
) {
    val colors = MaterialTheme.colorScheme
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
            verticalAlignment = Alignment.Top,
        ) {
            Text(
                text = stringResource(R.string.detail_step_n, stepNumber),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = colors.primary,
                modifier = Modifier
                    .width(72.dp)
                    .padding(end = Spacing.sm),
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = colors.onSurface,
                modifier = Modifier.weight(1f),
            )
        }
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = Spacing.lg),
                color = colors.outlineVariant,
            )
        }
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

@Preview(showBackground = true, name = "Instructions tab")
@Composable
private fun RecipeDetailInstructionsPreview() {
    RecipeBoxTheme {
        RecipeDetailScreen(
            state = RecipeDetailUiState(isLoading = false, recipe = SampleRecipes.curry),
            onIntent = {},
        )
    }
}
