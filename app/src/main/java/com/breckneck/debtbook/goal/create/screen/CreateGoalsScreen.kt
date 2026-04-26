package com.breckneck.debtbook.goal.create.screen

import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.breckneck.debtbook.R
import com.breckneck.debtbook.common.PreviewDark
import com.breckneck.debtbook.common.toDMYFormat
import com.breckneck.debtbook.core.ui.components.DebtBookLargeTopAppBar
import com.breckneck.debtbook.core.ui.components.SettingsPickerBottomSheet
import com.breckneck.debtbook.core.ui.theme.DebtBookTheme
import com.breckneck.debtbook.core.ui.theme.spacing
import com.breckneck.debtbook.goal.create.CreateGoalsAction
import com.breckneck.debtbook.goal.create.CreateGoalsState
import com.breckneck.debtbook.goal.create.CreateGoalsViewModel
import com.breckneck.debtbook.goal.create.model.NameError
import com.breckneck.debtbook.goal.create.model.SavedSumError
import com.breckneck.debtbook.goal.create.model.SumError
import com.breckneck.debtbook.goal.create.model.CreateGoalUi
import org.orbitmvi.orbit.compose.collectAsState
import java.util.Calendar
import java.util.Date

@Composable
private fun NameError.message(): String = when (this) {
    NameError.EMPTY -> stringResource(R.string.youmustentername)
}

@Composable
private fun SumError.message(): String = when (this) {
    SumError.ZERO     -> stringResource(R.string.zerodebt)
    SumError.INVALID  -> stringResource(R.string.something_went_wrong)
    SumError.NEGATIVE -> stringResource(R.string.goal_sum_must_be_positive)
}

@Composable
private fun SavedSumError.message(): String = when (this) {
    SavedSumError.GREATER_THAN_SUM -> stringResource(R.string.already_saved_sum_can_t_be_greater_than_the_goal_sum)
    SavedSumError.INVALID          -> stringResource(R.string.something_went_wrong)
}

@Composable
fun CreateGoalsScreen(vm: CreateGoalsViewModel) {
    val state by vm.collectAsState()
    CreateGoalsContent(state = state, onAction = vm::onAction)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CreateGoalsContent(
    state: CreateGoalsState,
    onAction: (CreateGoalsAction) -> Unit,
) {
    val spacing = MaterialTheme.spacing
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val title = if (state.isEditMode) stringResource(R.string.edit) else stringResource(R.string.add_new_goal)

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            DebtBookLargeTopAppBar(
                title = title,
                scrollBehavior = scrollBehavior,
                onBackClick = { onAction(CreateGoalsAction.BackClick) },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onAction(CreateGoalsAction.SaveClick) }) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = stringResource(R.string.save),
                )
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing.space8, vertical = spacing.space8),
            verticalArrangement = Arrangement.spacedBy(spacing.space8),
        ) {
            NameCard(
                name = state.goal.name,
                nameError = state.nameError,
                onNameChanged = { onAction(CreateGoalsAction.NameChanged(it)) },
            )

            PhotoCard(
                imageUri = state.goal.imageUri,
                imagePath = state.goal.imagePath,
                hasImage = state.goal.hasImage,
                onPhotoCardClick = { onAction(CreateGoalsAction.PhotoCardClick) },
                onDeleteImage = { onAction(CreateGoalsAction.DeleteImage) },
            )

            SumCard(
                sum = state.goal.sum,
                sumError = state.sumError,
                savedSum = state.goal.savedSum,
                savedSumError = state.savedSumError,
                currencyDisplayName = state.goal.currencyDisplayName,
                onSumChanged = { onAction(CreateGoalsAction.SumChanged(it)) },
                onSavedSumChanged = { onAction(CreateGoalsAction.SavedSumChanged(it)) },
                onCurrencyClick = { onAction(CreateGoalsAction.CurrencyClick) },
            )

            DateCard(
                dateFormatted = state.goal.goalDate?.toDMYFormat(),
                onClick = { onAction(CreateGoalsAction.DateClick) },
            )

            Spacer(modifier = Modifier.height(72.dp))
        }
    }

    if (state.isCurrencySheetVisible) {
        SettingsPickerBottomSheet(
            title = stringResource(R.string.select_currency),
            options = state.currencyNames,
            selectedIndex = state.selectedCurrencyIndex,
            onItemSelected = { onAction(CreateGoalsAction.CurrencySelected(it)) },
            onDismiss = { onAction(CreateGoalsAction.DismissCurrencySheet) },
        )
    }

    if (state.isDatePickerVisible) {
        GoalDatePickerDialog(
            currentDateMs = state.goal.goalDate?.time,
            onDateSelected = { ms ->
                if (ms != null) onAction(CreateGoalsAction.DateSelected(Date(ms)))
                else onAction(CreateGoalsAction.DismissDatePicker)
            },
            onDismiss = { onAction(CreateGoalsAction.DismissDatePicker) },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GoalDatePickerDialog(
    currentDateMs: Long?,
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit,
) {
    val minSelectableMillis = remember {
        Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    val selectableDates = remember(minSelectableMillis) {
        object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                utcTimeMillis >= minSelectableMillis

            override fun isSelectableYear(year: Int): Boolean {
                val minYear = Calendar.getInstance().apply {
                    timeInMillis = minSelectableMillis
                }.get(Calendar.YEAR)
                return year >= minYear
            }
        }
    }

    val initialMs = if (currentDateMs != null && currentDateMs >= minSelectableMillis) {
        currentDateMs
    } else {
        minSelectableMillis
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialMs,
        selectableDates = selectableDates,
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onDateSelected(datePickerState.selectedDateMillis) }) {
                Text(stringResource(android.R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(android.R.string.cancel))
            }
        },
    ) {
        DatePicker(state = datePickerState)
    }
}

@Composable
private fun NameCard(
    name: String,
    nameError: NameError?,
    onNameChanged: (String) -> Unit,
) {
    val spacing = MaterialTheme.spacing
    FormCard {
        OutlinedTextField(
            value = name,
            onValueChange = { if (it.length <= 20) onNameChanged(it) },
            label = { Text(stringResource(R.string.name)) },
            isError = nameError != null,
            supportingText = nameError?.let { error -> { Text(error.message()) } },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.space8, vertical = spacing.space16),
        )
    }
}

@Composable
private fun PhotoCard(
    imageUri: Uri?,
    imagePath: String?,
    hasImage: Boolean,
    onPhotoCardClick: () -> Unit,
    onDeleteImage: () -> Unit,
) {
    val spacing = MaterialTheme.spacing
    FormCard(
        modifier = Modifier.clickable(enabled = !hasImage, onClick = onPhotoCardClick),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.space12),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(R.string.goal_s_photo),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (hasImage) {
                    IconButton(onClick = onDeleteImage) {
                        Icon(
                            imageVector = Icons.Outlined.DeleteOutline,
                            contentDescription = stringResource(R.string.delete),
                            tint = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(spacing.space12))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f),
            ) {
                if (hasImage) {
                    AsyncImage(
                        model = imagePath ?: imageUri,
                        contentDescription = stringResource(R.string.goal_s_photo),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.CameraAlt,
                        contentDescription = stringResource(R.string.goal_s_photo),
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun SumCard(
    sum: String,
    sumError: SumError?,
    savedSum: String,
    savedSumError: SavedSumError?,
    currencyDisplayName: String,
    onSumChanged: (String) -> Unit,
    onSavedSumChanged: (String) -> Unit,
    onCurrencyClick: () -> Unit,
) {
    val spacing = MaterialTheme.spacing
    val isDark = isSystemInDarkTheme()

    FormCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.space8, vertical = spacing.space16),
            verticalAlignment = Alignment.Top,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(spacing.space8),
            ) {
                OutlinedTextField(
                    value = sum,
                    onValueChange = onSumChanged,
                    label = { Text(stringResource(R.string.sum)) },
                    isError = sumError != null,
                    supportingText = sumError?.let { error -> { Text(error.message()) } },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = savedSum,
                    onValueChange = onSavedSumChanged,
                    label = { Text(stringResource(R.string.already_saved_sum)) },
                    isError = savedSumError != null,
                    supportingText = savedSumError?.let { error -> { Text(error.message()) } },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Column(
                modifier = Modifier.padding(start = spacing.space8, top = spacing.space4),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                TextButton(onClick = onCurrencyClick) {
                    Text(
                        text = currencyDisplayName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isDark) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

@Composable
private fun DateCard(
    dateFormatted: String?,
    onClick: () -> Unit,
) {
    val spacing = MaterialTheme.spacing
    FormCard(modifier = Modifier.clickable(onClick = onClick)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.space16, vertical = spacing.space16),
        ) {
            Icon(
                imageVector = Icons.Outlined.DateRange,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(end = spacing.space12),
            )
            Column {
                Text(
                    text = stringResource(R.string.goal_deadline),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = dateFormatted ?: stringResource(R.string.not_selected),
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (dateFormatted != null) MaterialTheme.colorScheme.onSurface
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun FormCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val isDark = isSystemInDarkTheme()
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) MaterialTheme.colorScheme.surfaceContainerHigh
                             else MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        content()
    }
}

// region Previews

@Preview(name = "CreateGoals — create, light")
@PreviewDark
@Composable
private fun CreateGoalsCreatePreview() {
    DebtBookTheme(dynamicColor = false) {
        Surface {
            CreateGoalsContent(
                state = CreateGoalsState.initial().copy(
                    goal = CreateGoalUi(
                        currency = "$",
                        currencyDisplayName = "USD $",
                    ),
                    selectedCurrencyIndex = 0,
                ),
                onAction = {},
            )
        }
    }
}

@Preview(name = "CreateGoals — edit, light")
@Preview(name = "CreateGoals — edit, dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CreateGoalsEditPreview() {
    DebtBookTheme(dynamicColor = false) {
        Surface {
            CreateGoalsContent(
                state = CreateGoalsState.initial().copy(
                    isEditMode = true,
                    goal = CreateGoalUi(
                        name = "New laptop",
                        sum = "1500",
                        savedSum = "300",
                        currency = "$",
                        currencyDisplayName = "USD $",
                    ),
                    selectedCurrencyIndex = 0,
                ),
                onAction = {},
            )
        }
    }
}

@Preview(name = "CreateGoals — with errors, light")
@Composable
private fun CreateGoalsErrorsPreview() {
    DebtBookTheme(dynamicColor = false) {
        Surface {
            CreateGoalsContent(
                state = CreateGoalsState.initial().copy(
                    goal = CreateGoalUi(
                        currency = "$",
                        currencyDisplayName = "USD $",
                    ),
                    nameError = NameError.EMPTY,
                    sumError = SumError.ZERO,
                ),
                onAction = {},
            )
        }
    }
}

// endregion
