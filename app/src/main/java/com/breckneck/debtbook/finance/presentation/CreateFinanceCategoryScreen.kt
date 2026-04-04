package com.breckneck.debtbook.finance.presentation

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.breckneck.debtbook.R
import com.breckneck.debtbook.core.ui.components.DebtBookTopBar
import com.breckneck.debtbook.core.ui.theme.DebtBookTheme
import com.breckneck.debtbook.core.ui.theme.spacing
import com.breckneck.debtbook.finance.presentation.model.emojiGroups
import com.breckneck.debtbook.finance.viewmodel.CreateFinanceCategoryState
import com.breckneck.debtbook.finance.viewmodel.CreateFinanceCategorySideEffect
import com.breckneck.debtbook.finance.viewmodel.CreateFinanceCategoryViewModel
import com.breckneck.deptbook.domain.util.categoryColorList
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

private val errorEnterAnimation: EnterTransition = expandVertically() + fadeIn(tween(200))
private val errorExitAnimation: ExitTransition = shrinkVertically() + fadeOut(tween(150))

private const val EMOJI_GRID_COLUMNS = 5

@Composable
fun CreateFinanceCategoryScreen(
    vm: CreateFinanceCategoryViewModel,
    onBackClick: () -> Unit,
    onCategorySaved: () -> Unit
) {
    val state by vm.collectAsState()

    vm.collectSideEffect { effect ->
        when (effect) {
            CreateFinanceCategorySideEffect.CategorySaved -> onCategorySaved()
        }
    }

    CreateFinanceCategoryContent(
        state = state,
        onBackClick = onBackClick,
        onNameChange = vm::onNameChange,
        onImageSelected = vm::onImageSelected,
        onColorSelected = vm::onColorSelected,
        onSaveClick = vm::onSaveClick,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateFinanceCategoryContent(
    state: CreateFinanceCategoryState,
    onBackClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onImageSelected: (image: Int) -> Unit,
    onColorSelected: (color: String) -> Unit,
    onSaveClick: () -> Unit
) {
    val spacing = MaterialTheme.spacing
    val nameError = if (state.isNameErrorVisible) stringResource(R.string.youmustentername) else null
    val imageError = if (state.isImageErrorVisible) stringResource(R.string.you_must_select_image) else null
    val colorError = if (state.isColorErrorVisible) stringResource(R.string.you_must_select_color) else null

    Scaffold(
        topBar = {
            DebtBookTopBar(
                title = stringResource(R.string.create_new_category),
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onSaveClick) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = stringResource(R.string.create_new_category)
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            CategoryPreview(
                name = state.categoryName,
                selectedImage = state.selectedImage,
                selectedColor = state.selectedColor
            )

            Spacer(modifier = Modifier.height(spacing.space24))

            OutlinedTextField(
                value = state.categoryName,
                onValueChange = onNameChange,
                label = { Text(stringResource(R.string.name)) },
                isError = state.isNameErrorVisible,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.space16)
            )
            SectionError(error = nameError, startPadding = spacing.space32)

            Spacer(modifier = Modifier.height(spacing.space24))

            Text(
                text = stringResource(R.string.image),
                style = MaterialTheme.typography.titleMedium,
                color = if (state.isImageErrorVisible)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = spacing.space16, bottom = spacing.space8)
            )

            val pagerState = rememberPagerState(pageCount = { emojiGroups.size })
            val coroutineScope = rememberCoroutineScope()

            SecondaryScrollableTabRow(
                selectedTabIndex = pagerState.currentPage,
                edgePadding = spacing.space16,
                divider = {},
                containerColor = Color.Transparent,
            ) {
                emojiGroups.forEachIndexed { index, group ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch { pagerState.animateScrollToPage(index) }
                        },
                        text = { Text(text = stringResource(group.labelResId)) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.space8))

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth()
            ) { page ->
                EmojiGrid(
                    emojis = emojiGroups[page].emojis,
                    selectedImage = state.selectedImage,
                    onImageSelected = onImageSelected,
                    modifier = Modifier.padding(horizontal = spacing.space16)
                )
            }

            SectionError(error = imageError, startPadding = spacing.space16)

            Spacer(modifier = Modifier.height(spacing.space24))

            Text(
                text = stringResource(R.string.color),
                style = MaterialTheme.typography.titleMedium,
                color = if (state.isColorErrorVisible)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = spacing.space16, bottom = spacing.space8)
            )

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.space16),
                horizontalArrangement = Arrangement.spacedBy(spacing.space12),
                verticalArrangement = Arrangement.spacedBy(spacing.space12)
            ) {
                categoryColorList.forEach { color ->
                    CategoryColorItem(
                        colorHex = color,
                        isSelected = state.selectedColor == color,
                        onClick = { onColorSelected(color) }
                    )
                }
            }

            SectionError(error = colorError, startPadding = spacing.space16)

            Spacer(modifier = Modifier.height(spacing.space24))
        }
    }
}

@Composable
private fun SectionError(
    error: String?,
    startPadding: Dp
) {
    val spacing = MaterialTheme.spacing
    AnimatedVisibility(
        visible = error != null,
        enter = errorEnterAnimation,
        exit = errorExitAnimation
    ) {
        Text(
            text = error.orEmpty(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(start = startPadding, top = spacing.space4)
        )
    }
}

@Composable
private fun CategoryPreview(
    name: String,
    selectedImage: Int?,
    selectedColor: String?
) {
    val bgColor = if (selectedColor != null)
        Color(selectedColor.toColorInt())
    else
        MaterialTheme.colorScheme.surfaceContainerHighest

    val spacing = MaterialTheme.spacing

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.space16)
            .padding(top = spacing.space16),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.space24),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                if (selectedImage != null) {
                    Text(
                        text = String(Character.toChars(selectedImage)),
                        fontSize = 32.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.space12))

            Text(
                text = name.ifBlank { stringResource(R.string.name) },
                style = if (name.isNotBlank())
                    MaterialTheme.typography.titleMedium
                else
                    MaterialTheme.typography.bodyMedium,
                color = if (name.isNotBlank())
                    MaterialTheme.colorScheme.onSurface
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EmojiGrid(
    emojis: List<Int>,
    selectedImage: Int?,
    onImageSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacing.space8)
    ) {
        emojis.chunked(EMOJI_GRID_COLUMNS).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.space8)
            ) {
                row.forEach { codePoint ->
                    CategoryImageItem(
                        image = codePoint,
                        isSelected = selectedImage == codePoint,
                        onClick = { onImageSelected(codePoint) },
                        modifier = Modifier.weight(1f)
                    )
                }
                repeat(EMOJI_GRID_COLUMNS - row.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun CategoryImageItem(
    image: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.08f else 1f,
        animationSpec = tween(200),
        label = "imageScale"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isSelected)
            MaterialTheme.colorScheme.primary
        else
            MaterialTheme.colorScheme.outlineVariant,
        animationSpec = tween(200),
        label = "imageBorder"
    )

    Surface(
        onClick = onClick,
        modifier = modifier
            .aspectRatio(1f)
            .scale(scale),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = borderColor
        )
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = String(Character.toChars(image)),
                fontSize = 28.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CategoryColorItem(
    colorHex: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val parsedColor = Color(colorHex.toColorInt())
    val checkTint = if (parsedColor.luminance() > 0.4f) Color.Black else Color.White

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.15f else 1f,
        animationSpec = tween(200),
        label = "colorScale"
    )

    Surface(
        onClick = onClick,
        modifier = Modifier
            .size(40.dp)
            .scale(scale),
        shape = CircleShape,
        color = parsedColor,
        border = if (isSelected) BorderStroke(
            width = 2.5.dp,
            color = MaterialTheme.colorScheme.onSurface
        ) else null
    ) {
        if (isSelected) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = checkTint,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Preview(name = "Empty (light)")
@Preview(name = "Empty (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CreateFinanceCategoryPreviewEmpty() {
    DebtBookTheme(dynamicColor = false) {
        CreateFinanceCategoryContent(
            state = CreateFinanceCategoryState.initial(),
            onBackClick = {},
            onNameChange = {},
            onImageSelected = {},
            onColorSelected = {},
            onSaveClick = {}
        )
    }
}

@Preview(name = "Filled (light)")
@Preview(name = "Filled (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CreateFinanceCategoryPreviewFilled() {
    DebtBookTheme(dynamicColor = false) {
        CreateFinanceCategoryContent(
            state = CreateFinanceCategoryState.initial().copy(
                categoryName = "Food",
                selectedImage = 0x1F37D,
                selectedColor = "#A5D6A7"
            ),
            onBackClick = {},
            onNameChange = {},
            onImageSelected = {},
            onColorSelected = {},
            onSaveClick = {}
        )
    }
}

@Preview(name = "Validation error")
@Composable
private fun CreateFinanceCategoryPreviewError() {
    DebtBookTheme(dynamicColor = false) {
        CreateFinanceCategoryContent(
            state = CreateFinanceCategoryState.initial().copy(
                isNameErrorVisible = true,
                isImageErrorVisible = true,
                isColorErrorVisible = true
            ),
            onBackClick = {},
            onNameChange = {},
            onImageSelected = {},
            onColorSelected = {},
            onSaveClick = {}
        )
    }
}
