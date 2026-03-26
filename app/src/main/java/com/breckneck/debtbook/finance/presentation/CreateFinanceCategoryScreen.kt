package com.breckneck.debtbook.finance.presentation

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.breckneck.debtbook.R
import com.breckneck.debtbook.core.ui.components.DebtBookTopBar
import com.breckneck.debtbook.core.ui.theme.DebtBookTheme
import com.breckneck.debtbook.finance.viewmodel.CreateFinanceCategoryViewModel
import com.breckneck.deptbook.domain.model.FinanceCategory
import com.breckneck.deptbook.domain.util.categoryColorList
import com.breckneck.deptbook.domain.util.categoryImageList

@Composable
fun CreateFinanceCategoryScreen(
    vm: CreateFinanceCategoryViewModel,
    onBackClick: () -> Unit,
    onCategorySaved: () -> Unit
) {
    val checkedImagePosition by vm.checkedImagePosition.observeAsState()
    val checkedColorPosition by vm.checkedColorPosition.observeAsState()
    val checkedImage by vm.checkedImage.observeAsState()
    val checkedColor by vm.checkedColor.observeAsState()
    val financeCategoryState by vm.financeCategoryState.observeAsState()

    var categoryName by rememberSaveable { mutableStateOf("") }
    var nameError by rememberSaveable { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val mustEnterNameError = stringResource(R.string.youmustentername)
    val mustSelectColorError = stringResource(R.string.you_must_select_color)
    val mustSelectImageError = stringResource(R.string.you_must_select_image)

    CreateFinanceCategoryContent(
        categoryName = categoryName,
        nameError = nameError,
        selectedImageIndex = checkedImagePosition,
        selectedColorIndex = checkedColorPosition,
        onBackClick = onBackClick,
        onNameChange = { newValue ->
            if (newValue.length <= 20) {
                categoryName = newValue
                if (newValue.trim().isNotEmpty()) nameError = null
            }
        },
        onImageSelected = { index, image ->
            vm.setCheckedImage(image)
            vm.setCheckedImagePosition(index)
        },
        onColorSelected = { index, color ->
            vm.setCheckedColor(color)
            vm.setCheckedColorPosition(index)
        },
        onSaveClick = {
            var isValid = true

            if (categoryName.trim().isEmpty()) {
                nameError = mustEnterNameError
                isValid = false
            } else {
                nameError = null
            }

            if (checkedColor == null) {
                Toast.makeText(context, mustSelectColorError, Toast.LENGTH_SHORT).show()
                isValid = false
            }

            if (checkedImage == null) {
                Toast.makeText(context, mustSelectImageError, Toast.LENGTH_SHORT).show()
                isValid = false
            }

            if (isValid && financeCategoryState != null) {
                vm.setFinanceCategory(
                    FinanceCategory(
                        name = categoryName.trim(),
                        color = checkedColor!!,
                        state = financeCategoryState!!,
                        image = checkedImage!!
                    )
                )
                onCategorySaved()
            }
        }
    )
}

@Composable
fun CreateFinanceCategoryContent(
    categoryName: String,
    nameError: String?,
    selectedImageIndex: Int?,
    selectedColorIndex: Int?,
    onBackClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onImageSelected: (index: Int, image: Int) -> Unit,
    onColorSelected: (index: Int, color: String) -> Unit,
    onSaveClick: () -> Unit
) {
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
            OutlinedTextField(
                value = categoryName,
                onValueChange = onNameChange,
                label = { Text(stringResource(R.string.name)) },
                isError = nameError != null,
                supportingText = nameError?.let { error -> { Text(error) } },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .padding(top = 16.dp)
            )

            HorizontalDivider(modifier = Modifier.padding(top = 8.dp))

            Text(
                text = stringResource(R.string.image),
                fontSize = 24.sp,
                modifier = Modifier.padding(start = 8.dp, top = 16.dp, bottom = 16.dp)
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 8.dp),
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

            HorizontalDivider(modifier = Modifier.padding(top = 16.dp))

            Text(
                text = stringResource(R.string.color),
                fontSize = 24.sp,
                modifier = Modifier.padding(start = 8.dp, top = 16.dp, bottom = 16.dp)
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                itemsIndexed(categoryColorList) { index, color ->
                    CategoryColorItem(
                        colorHex = color,
                        isSelected = selectedColorIndex == index,
                        onClick = { onColorSelected(index, color) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryImageItem(
    image: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(56.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.outline
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
    Card(
        modifier = Modifier
            .size(48.dp)
            .clickable(onClick = onClick),
        shape = CircleShape,
        colors = CardDefaults.cardColors(
            containerColor = Color(android.graphics.Color.parseColor(colorHex))
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
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
            categoryName = "",
            nameError = null,
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
            nameError = "You didn't enter all the information",
            selectedImageIndex = 2,
            selectedColorIndex = null,
            onBackClick = {},
            onNameChange = {},
            onImageSelected = { _, _ -> },
            onColorSelected = { _, _ -> },
            onSaveClick = {}
        )
    }
}
