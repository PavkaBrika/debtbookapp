package com.breckneck.debtbook.finance.presentation

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.breckneck.debtbook.R
import com.breckneck.debtbook.core.ui.components.DebtBookTopBar
import com.breckneck.debtbook.core.ui.components.EmptyListPlaceholder
import com.breckneck.debtbook.core.ui.components.ExtraFunctionsBottomSheet
import com.breckneck.debtbook.core.ui.components.ShimmerListPlaceholder
import com.breckneck.debtbook.core.ui.theme.DebtBookTheme
import com.breckneck.debtbook.core.ui.theme.spacing
import com.breckneck.debtbook.finance.util.GetFinanceCategoryNameInLocalLanguage
import com.breckneck.debtbook.finance.viewmodel.FinanceDetailsViewModel
import com.breckneck.deptbook.domain.model.Finance
import com.breckneck.deptbook.domain.util.FinanceCategoryState
import com.breckneck.deptbook.domain.util.ListState
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FinanceDetailsScreen(
    vm: FinanceDetailsViewModel,
    categoryName: String,
    categoryId: Int,
    isExpenses: Boolean,
    currency: String,
    onBackClick: () -> Unit,
    onEditFinanceClick: (Finance) -> Unit
) {
    val context = LocalContext.current

    val financeList by vm.financeList.observeAsState(emptyList())
    val financeListState by vm.financeListState.observeAsState(ListState.LOADING)
    val isSettingsDialogOpened by vm.isSettingsDialogOpened.observeAsState(false)
    val settingsFinance by vm.settingsFinance.observeAsState()

    LaunchedEffect(categoryId) {
        if (vm.categoryId.value == null) {
            vm.setCategoryId(categoryId)
            vm.setExpenses(isExpenses)
            vm.getFinanceByCategoryId(categoryId = categoryId)
        }
    }

    val localizedCategoryName = remember(categoryName, isExpenses) {
        GetFinanceCategoryNameInLocalLanguage().execute(
            financeName = categoryName,
            state = if (isExpenses) FinanceCategoryState.EXPENSE else FinanceCategoryState.INCOME,
            context = context
        )
    }

    val subtitle = if (isExpenses)
        stringResource(R.string.expenses)
    else
        stringResource(R.string.revenues)

    FinanceDetailsContent(
        title = localizedCategoryName,
        subtitle = subtitle,
        currency = currency,
        financeList = financeList,
        financeListState = financeListState,
        isExpenses = isExpenses,
        onBackClick = onBackClick,
        onFinanceClick = { finance ->
            vm.onFinanceSettingsDialogOpen()
            vm.onSetSettingFinance(finance = finance)
        }
    )

    if (isSettingsDialogOpened && settingsFinance != null) {
        val sdf = remember { SimpleDateFormat("d MMM yyyy", Locale.getDefault()) }
        val sheetTitle = remember(settingsFinance) {
            "${sdf.format(settingsFinance!!.date)} : ${formatFinanceSum(settingsFinance!!.sum)} $currency"
        }
        ExtraFunctionsBottomSheet(
            title = sheetTitle,
            onEdit = {
                vm.onFinanceSettingsDialogClose()
                onEditFinanceClick(settingsFinance!!)
            },
            onDelete = {
                vm.deleteFinance(finance = settingsFinance!!)
                vm.onFinanceSettingsDialogClose()
            },
            onDismiss = {
                vm.onFinanceSettingsDialogClose()
            }
        )
    }
}

@Composable
internal fun FinanceDetailsContent(
    title: String,
    subtitle: String,
    currency: String,
    financeList: List<Finance>,
    financeListState: ListState,
    isExpenses: Boolean,
    onBackClick: () -> Unit,
    onFinanceClick: (Finance) -> Unit
) {
    Scaffold(
        topBar = {
            DebtBookTopBar(
                title = title,
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        AnimatedContent(
            targetState = financeListState,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "financeListState",
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) { state ->
            when (state) {
                ListState.LOADING -> ShimmerListPlaceholder(rowCount = 6, rowHeight = 80.dp)

                ListState.EMPTY -> EmptyListPlaceholder(
                    emptyText = if (isExpenses)
                        stringResource(R.string.there_are_no_expenses_yet)
                    else
                        stringResource(R.string.there_are_no_incomes_yet),
                    emptyIcon = Icons.Filled.ReceiptLong
                )

                ListState.RECEIVED -> FinanceList(
                    subtitle = subtitle,
                    currency = currency,
                    financeList = financeList,
                    onFinanceClick = onFinanceClick
                )
            }
        }
    }
}

@Composable
private fun FinanceList(
    subtitle: String,
    currency: String,
    financeList: List<Finance>,
    onFinanceClick: (Finance) -> Unit
) {
    val spacing = MaterialTheme.spacing
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = spacing.space16,
            vertical = spacing.space8
        ),
        verticalArrangement = Arrangement.spacedBy(spacing.space8)
    ) {
        item(key = "subtitle") {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = spacing.space4)
            )
        }
        items(financeList, key = { it.id }) { finance ->
            FinanceItem(
                finance = finance,
                currency = currency,
                onClick = { onFinanceClick(finance) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FinanceItem(
    finance: Finance,
    currency: String,
    onClick: () -> Unit
) {
    val spacing = MaterialTheme.spacing
    val decimalFormat = remember {
        val symbols = DecimalFormatSymbols().apply { groupingSeparator = ' ' }
        DecimalFormat("###,###,###.##", symbols)
    }
    val sdf = remember { SimpleDateFormat("d MMM yyyy", Locale.getDefault()) }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.space16)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = decimalFormat.format(finance.sum),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = currency,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(spacing.space4))
            Text(
                text = sdf.format(finance.date),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (!finance.info.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(spacing.space4))
                Text(
                    text = finance.info!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun formatFinanceSum(sum: Double): String {
    val symbols = DecimalFormatSymbols().apply { groupingSeparator = ' ' }
    return DecimalFormat("###,###,###.##", symbols).format(sum)
}

// region Previews

@Preview(name = "Loading — light")
@Preview(name = "Loading — dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FinanceDetailsLoadingPreview() {
    DebtBookTheme(dynamicColor = false) {
        Surface {
            FinanceDetailsContent(
                title = "Food",
                subtitle = "Expenses",
                currency = "USD",
                financeList = emptyList(),
                financeListState = ListState.LOADING,
                isExpenses = true,
                onBackClick = {},
                onFinanceClick = {}
            )
        }
    }
}

@Preview(name = "Empty — light")
@Preview(name = "Empty — dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FinanceDetailsEmptyPreview() {
    DebtBookTheme(dynamicColor = false) {
        Surface {
            FinanceDetailsContent(
                title = "Food",
                subtitle = "Expenses",
                currency = "USD",
                financeList = emptyList(),
                financeListState = ListState.EMPTY,
                isExpenses = true,
                onBackClick = {},
                onFinanceClick = {}
            )
        }
    }
}

@Preview(name = "Filled — light")
@Preview(name = "Filled — dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FinanceDetailsFilledPreview() {
    DebtBookTheme(dynamicColor = false) {
        Surface {
            FinanceDetailsContent(
                title = "Food",
                subtitle = "Expenses",
                currency = "USD",
                financeList = listOf(
                    Finance(id = 1, sum = 1500.0, date = Date(), info = "Lunch", financeCategoryId = 1),
                    Finance(id = 2, sum = 250.50, date = Date(), info = null, financeCategoryId = 1),
                    Finance(id = 3, sum = 89.99, date = Date(), info = "Coffee & snacks", financeCategoryId = 1),
                ),
                financeListState = ListState.RECEIVED,
                isExpenses = true,
                onBackClick = {},
                onFinanceClick = {}
            )
        }
    }
}

// endregion
