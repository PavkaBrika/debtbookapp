package com.breckneck.debtbook.goal.create.screen

import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.VerticalDivider
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import com.breckneck.debtbook.R
import com.breckneck.debtbook.common.toDMYFormat
import com.breckneck.debtbook.core.ui.components.DebtBookLargeTopAppBar
import com.breckneck.debtbook.core.ui.components.SettingsPickerBottomSheet
import com.breckneck.debtbook.core.ui.theme.DebtBookTheme
import com.breckneck.debtbook.core.ui.theme.LocalDebtBookDarkTheme
import com.breckneck.debtbook.core.ui.theme.elevation
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

            Spacer(modifier = Modifier.height(spacing.space48 + spacing.space24))
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
    // DatePicker передаёт utcTimeMillis → граница тоже должна быть в UTC
    val minSelectableMillis = remember {
        Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC")).apply {
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
                val minYear = Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC")).apply {
                    timeInMillis = minSelectableMillis
                }.get(Calendar.YEAR)
                return year >= minYear
            }
        }
    }

    // null = нет пре-выбора; не фоллбэчим на minSelectableMillis чтобы не подсвечивать дату которую нельзя выбрать
    val initialMs = currentDateMs?.takeIf { it >= minSelectableMillis }

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
                .padding(horizontal = spacing.space16, vertical = spacing.space16),
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
        modifier = Modifier.clickable(onClick = onPhotoCardClick),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.space16, vertical = spacing.space12),
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
                        modifier = Modifier.size(spacing.space48),
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

    FormCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = spacing.space16, top = spacing.space16, bottom = spacing.space16),
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
            VerticalDivider(modifier = Modifier.padding(horizontal = spacing.space8))
            val currencyPickerA11y = stringResource(
                R.string.a11y_currency_picker,
                currencyDisplayName,
            )
            TextButton(
                onClick = onCurrencyClick,
                modifier = Modifier
                    .defaultMinSize(minWidth = spacing.space48, minHeight = spacing.space48)
                    .padding(end = spacing.space4)
                    .semantics(mergeDescendants = true) {
                        contentDescription = currencyPickerA11y
                    },
            ) {
                Text(
                    text = currencyDisplayName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
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
    val isDark = LocalDebtBookDarkTheme.current
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) MaterialTheme.colorScheme.surfaceContainerHigh
                             else MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevation.card),
    ) {
        content()
    }
}

// region Previews

@Preview(
    name = "Create — light",
    group = "CreateGoals",
)
@Preview(
    name = "Create — dark",
    group = "CreateGoals",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun CreateGoalsCreatePreview() {
    DebtBookTheme(dynamicColor = false) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
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

@Preview(
    name = "Edit — light",
    group = "CreateGoals",
)
@Preview(
    name = "Edit — dark",
    group = "CreateGoals",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun CreateGoalsEditPreview() {
    DebtBookTheme(dynamicColor = false) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
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

@Preview(
    name = "Errors — light",
    group = "CreateGoals",
)
@Preview(
    name = "Errors — dark",
    group = "CreateGoals",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun CreateGoalsErrorsPreview() {
    DebtBookTheme(dynamicColor = false) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
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
