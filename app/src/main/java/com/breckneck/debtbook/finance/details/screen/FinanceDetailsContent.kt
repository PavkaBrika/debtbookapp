package com.breckneck.debtbook.finance.details.screen

import android.content.res.Configuration
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.breckneck.debtbook.R
import com.breckneck.debtbook.core.ui.components.DebtBookLargeTopAppBar
import com.breckneck.debtbook.core.ui.components.EmptyListPlaceholder
import com.breckneck.debtbook.core.ui.components.ShimmerListPlaceholder
import com.breckneck.debtbook.core.ui.theme.DebtBookTheme
import com.breckneck.deptbook.domain.model.Finance
import com.breckneck.deptbook.domain.util.ListState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FinanceDetailsContent(
    title: String,
    subtitle: String,
    isExpenses: Boolean,
    currency: String,
    financeList: List<Finance>,
    financeListState: ListState,
    onBackClick: () -> Unit,
    onFinanceClick: (Finance) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            DebtBookLargeTopAppBar(
                title = title,
                scrollBehavior = scrollBehavior,
                onBackClick = onBackClick,
            )
        }
    ) { paddingValues ->
        Crossfade(
            targetState = financeListState,
            label = "financeListState",
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) { state ->
            when (state) {
                ListState.LOADING -> ShimmerListPlaceholder(
                    rowCount = 6,
                    rowHeight = 72.dp,
                    modifier = Modifier.fillMaxSize(),
                )

                ListState.EMPTY -> EmptyListPlaceholder(
                    emptyText = if (isExpenses) {
                        stringResource(R.string.there_are_no_expenses_yet)
                    } else {
                        stringResource(R.string.there_are_no_incomes_yet)
                    },
                    emptyIcon = Icons.AutoMirrored.Filled.ReceiptLong,
                    modifier = Modifier.fillMaxSize(),
                )

                ListState.RECEIVED -> FinanceDetailsList(
                    subtitle = subtitle,
                    isExpenses = isExpenses,
                    currency = currency,
                    financeList = financeList,
                    onFinanceClick = onFinanceClick
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Content Loading - light")
@Preview(name = "Content Loading - dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FinanceDetailsContentLoadingPreview() {
    DebtBookTheme(dynamicColor = false) {
        Surface {
            FinanceDetailsContent(
                title = "Food",
                subtitle = "Expenses",
                isExpenses = true,
                currency = "USD",
                financeList = emptyList(),
                financeListState = ListState.LOADING,
                onBackClick = {},
                onFinanceClick = {}
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Content Empty - light")
@Preview(name = "Content Empty - dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FinanceDetailsContentEmptyPreview() {
    DebtBookTheme(dynamicColor = false) {
        Surface {
            FinanceDetailsContent(
                title = "Food",
                subtitle = "Expenses",
                isExpenses = true,
                currency = "USD",
                financeList = emptyList(),
                financeListState = ListState.EMPTY,
                onBackClick = {},
                onFinanceClick = {}
            )
        }
    }
}
