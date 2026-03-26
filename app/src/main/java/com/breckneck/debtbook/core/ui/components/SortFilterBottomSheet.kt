package com.breckneck.debtbook.core.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.SwapVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.breckneck.debtbook.R
import com.breckneck.debtbook.core.ui.theme.DebtBookTheme

/**
 * Sort + Filter bottom sheet (replaces dialog_sort.xml).
 * Used in: MainFragment (human sort/filter), DebtDetails (debt sort/filter).
 *
 * @param sortOptions   Localized display strings for sort options.
 * @param filterOptions Localized display strings for filter options.
 * @param showRememberChoice Whether to show the "Remember choice" checkbox.
 * @param onConfirm     Called with (selectedSortIndex, selectedFilterIndex, rememberChoice) on Confirm.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortFilterBottomSheet(
    sortOptions: List<String>,
    initialSortIndex: Int = 0,
    filterOptions: List<String>,
    initialFilterIndex: Int = 0,
    showRememberChoice: Boolean = true,
    initialRememberChoice: Boolean = false,
    onConfirm: (sortIndex: Int, filterIndex: Int, rememberChoice: Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        SortFilterContent(
            sortOptions = sortOptions,
            initialSortIndex = initialSortIndex,
            filterOptions = filterOptions,
            initialFilterIndex = initialFilterIndex,
            showRememberChoice = showRememberChoice,
            initialRememberChoice = initialRememberChoice,
            onConfirm = onConfirm,
            onDismiss = onDismiss
        )
    }
}

@Composable
internal fun SortFilterContent(
    sortOptions: List<String>,
    initialSortIndex: Int = 0,
    filterOptions: List<String>,
    initialFilterIndex: Int = 0,
    showRememberChoice: Boolean = true,
    initialRememberChoice: Boolean = false,
    onConfirm: (sortIndex: Int, filterIndex: Int, rememberChoice: Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedSort by remember { mutableIntStateOf(initialSortIndex) }
    var selectedFilter by remember { mutableIntStateOf(initialFilterIndex) }
    var rememberChoice by remember { mutableStateOf(initialRememberChoice) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(top = 8.dp, bottom = 24.dp)
    ) {
        // Sort section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.SwapVert,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stringResource(R.string.order_by),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
                sortOptions.forEachIndexed { index, option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedSort = index }
                            .heightIn(min = 44.dp)
                            .padding(horizontal = 4.dp)
                    ) {
                        RadioButton(
                            selected = index == selectedSort,
                            onClick = { selectedSort = index }
                        )
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
                if (showRememberChoice) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { rememberChoice = !rememberChoice }
                            .heightIn(min = 44.dp)
                            .padding(horizontal = 4.dp)
                    ) {
                        Checkbox(
                            checked = rememberChoice,
                            onCheckedChange = { rememberChoice = it }
                        )
                        Text(
                            text = stringResource(R.string.remember_choice),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Filter section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FilterList,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stringResource(R.string.filter),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
                filterOptions.forEachIndexed { index, option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedFilter = index }
                            .heightIn(min = 44.dp)
                            .padding(horizontal = 4.dp)
                    ) {
                        RadioButton(
                            selected = index == selectedFilter,
                            onClick = { selectedFilter = index }
                        )
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(1f))
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text(text = stringResource(R.string.cancel))
            }
            Button(
                onClick = { onConfirm(selectedSort, selectedFilter, rememberChoice) }
            ) {
                Text(text = stringResource(R.string.confirm))
            }
        }
    }
}

@Preview(name = "SortFilter — light")
@Preview(name = "SortFilter — dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SortFilterBottomSheetPreview() {
    DebtBookTheme(dynamicColor = false) {
        Surface {
            SortFilterContent(
                sortOptions = listOf("Date", "Creation date", "Debt sum"),
                initialSortIndex = 0,
                filterOptions = listOf("Show all debts", "Show positive debts", "Show negative debts"),
                initialFilterIndex = 0,
                showRememberChoice = true,
                initialRememberChoice = false,
                onConfirm = { _, _, _ -> },
                onDismiss = {}
            )
        }
    }
}
