package com.breckneck.debtbook.finance.presentation

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.breckneck.debtbook.R
import com.breckneck.debtbook.core.ui.components.DebtBookTopBar
import com.breckneck.debtbook.core.ui.theme.DebtBookTheme
import com.breckneck.debtbook.finance.viewmodel.CreateFinanceCategorySideEffect
import com.breckneck.debtbook.finance.viewmodel.CreateFinanceCategoryViewModel
import com.breckneck.deptbook.domain.util.categoryColorList
import com.breckneck.deptbook.domain.util.categoryImageList
import androidx.core.graphics.toColorInt

@Composable
fun CreateFinanceCategoryScreen(
    vm: CreateFinanceCategoryViewModel,
    onBackClick: () -> Unit,
    onCategorySaved: () -> Unit
) {
    val state by vm.collectAsState()

    val mustEnterNameError = stringResource(R.string.youmustentername)
    val mustSelectColorError = stringResource(R.string.you_must_select_color)
    val mustSelectImageError = stringResource(R.string.you_must_select_image)

    vm.collectSideEffect { effect ->
        when (effect) {
            CreateFinanceCategorySideEffect.CategorySaved -> onCategorySaved()
        }
    }

    CreateFinanceCategoryContent(
        categoryName = state.categoryName,
        nameError = state.nameError,
        imageError = state.imageError,
        colorError = state.colorError,
        selectedImageIndex = state.selectedImageIndex,
        selectedColorIndex = state.selectedColorIndex,
        onBackClick = onBackClick,
        onNameChange = vm::onNameChange,
        onImageSelected = vm::onImageSelected,
        onColorSelected = vm::onColorSelected,
        onSaveClick = { vm.onSaveClick(mustEnterNameError, mustSelectImageError, mustSelectColorError) }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateFinanceCategoryContent(
    categoryName: String,
    nameError: String?,
    imageError: String?,
    colorError: String?,
    selectedImageIndex: Int?,
    selectedColorIndex: Int?,
    onBackClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onImageSelected: (index: Int, image: Int) -> Unit,
    onColorSelected: (index: Int, color: String) -> Unit,
    onSaveClick: () -> Unit
) {
    val errorEnter = expandVertically() + fadeIn(tween(200))
    val errorExit = shrinkVertically() + fadeOut(tween(150))

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
                name = categoryName,
                selectedImageIndex = selectedImageIndex,
                selectedColorIndex = selectedColorIndex
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = categoryName,
                onValueChange = onNameChange,
                label = { Text(stringResource(R.string.name)) },
                isError = nameError != null,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            AnimatedVisibility(
                visible = nameError != null,
                enter = errorEnter,
                exit = errorExit
            ) {
                Text(
                    text = nameError.orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 32.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.image),
                style = MaterialTheme.typography.titleMedium,
                color = if (imageError != null)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(categoryImageList) { index, image ->
                    CategoryImageItem(
                        image = image,
                        isSelected = selectedImageIndex == index,
                        onClick = { onImageSelected(index, image) }
                    )
                }
            }

            AnimatedVisibility(
                visible = imageError != null,
                enter = errorEnter,
                exit = errorExit
            ) {
                Text(
                    text = imageError.orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 32.dp, top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.color),
                style = MaterialTheme.typography.titleMedium,
                color = if (colorError != null)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
            )

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                maxItemsInEachRow = 7
            ) {
                categoryColorList.forEachIndexed { index, color ->
                    CategoryColorItem(
                        colorHex = color,
                        isSelected = selectedColorIndex == index,
                        onClick = { onColorSelected(index, color) }
                    )
                }
            }

            AnimatedVisibility(
                visible = colorError != null,
                enter = errorEnter,
                exit = errorExit
            ) {
                Text(
                    text = colorError.orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 32.dp, top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CategoryPreview(
    name: String,
    selectedImageIndex: Int?,
    selectedColorIndex: Int?
) {
    val hasImage = selectedImageIndex != null

    val bgColor = if (selectedColorIndex != null)
        Color(categoryColorList[selectedColorIndex].toColorInt())
    else
        MaterialTheme.colorScheme.surfaceContainerHighest

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                if (hasImage) {
                    Text(
                        text = String(Character.toChars(categoryImageList[selectedImageIndex!!])),
                        fontSize = 32.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

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
private fun CategoryImageItem(
    image: Int,
    isSelected: Boolean,
    onClick: () -> Unit
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
        modifier = Modifier
            .size(56.dp)
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
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
    val parsedColor = Color(android.graphics.Color.parseColor(colorHex))
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.15f else 1f,
        animationSpec = tween(200),
        label = "colorScale"
    )

    Box(
        modifier = Modifier
            .size(40.dp)
            .scale(scale)
            .then(
                if (isSelected)
                    Modifier.border(
                        width = 2.5.dp,
                        color = MaterialTheme.colorScheme.onSurface,
                        shape = CircleShape
                    )
                else Modifier
            )
            .clip(CircleShape)
            .background(parsedColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview(name = "Empty (light)")
@Preview(name = "Empty (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CreateFinanceCategoryPreviewEmpty() {
    DebtBookTheme(dynamicColor = false) {
        CreateFinanceCategoryContent(
            categoryName = "",
            nameError = null,
            imageError = null,
            colorError = null,
            selectedImageIndex = null,
            selectedColorIndex = null,
            onBackClick = {},
            onNameChange = {},
            onImageSelected = { _, _ -> },
            onColorSelected = { _, _ -> },
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
            categoryName = "Food",
            nameError = null,
            imageError = null,
            colorError = null,
            selectedImageIndex = 5,
            selectedColorIndex = 3,
            onBackClick = {},
            onNameChange = {},
            onImageSelected = { _, _ -> },
            onColorSelected = { _, _ -> },
            onSaveClick = {}
        )
    }
}

@Preview(name = "Validation error")
@Composable
private fun CreateFinanceCategoryPreviewError() {
    DebtBookTheme(dynamicColor = false) {
        CreateFinanceCategoryContent(
            categoryName = "",
            nameError = "You must enter a name",
            imageError = "You must select an image",
            colorError = "You must select a color",
            selectedImageIndex = null,
            selectedColorIndex = null,
            onBackClick = {},
            onNameChange = {},
            onImageSelected = { _, _ -> },
            onColorSelected = { _, _ -> },
            onSaveClick = {}
        )
    }
}
