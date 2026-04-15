package com.breckneck.debtbook.finance.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.breckneck.debtbook.R
import com.breckneck.debtbook.core.ui.components.ExtraFunctionsBottomSheet
import com.breckneck.debtbook.finance.util.GetFinanceCategoryNameInLocalLanguage
import com.breckneck.debtbook.finance.viewmodel.FinanceDetailsActions
import com.breckneck.debtbook.finance.viewmodel.FinanceDetailsViewModel
import com.breckneck.deptbook.domain.model.Finance
import com.breckneck.deptbook.domain.util.FinanceCategoryState
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Locale
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun FinanceDetailsScreen(
    vm: FinanceDetailsViewModel,
    onBackClick: () -> Unit,
    onEditFinanceClick: (Finance) -> Unit
) {
    val context = LocalContext.current

    val state by vm.collectAsState()

    val localizedCategoryName = remember(state.categoryName, state.isExpenses) {
        GetFinanceCategoryNameInLocalLanguage().execute(
            financeName = state.categoryName,
            state = if (state.isExpenses) FinanceCategoryState.EXPENSE else FinanceCategoryState.INCOME,
            context = context
        )
    }
    val subtitle =
        if (state.isExpenses) stringResource(R.string.expenses) else stringResource(R.string.revenues)

    val sheetSdf = remember { SimpleDateFormat("d MMM yyyy", Locale.getDefault()) }
    val sheetDecimalFormat = remember {
        val symbols = DecimalFormatSymbols().apply { groupingSeparator = ' ' }
        DecimalFormat("###,###,###.##", symbols)
    }

    FinanceDetailsContent(
        title = localizedCategoryName,
        subtitle = subtitle,
        isExpenses = state.isExpenses,
        currency = state.currency,
        financeList = state.financeList,
        financeListState = state.financeListState,
        onBackClick = onBackClick,
        onFinanceClick = { finance ->
            vm.onAction(FinanceDetailsActions.OpenFinanceSheet(finance = finance))
        }
    )

    if (state.isSettingsDialogOpened && state.settingsFinance != null) {
        val settingsFinance = state.settingsFinance!!
        val sheetTitle = remember(settingsFinance, state.currency) {
            "${sheetSdf.format(settingsFinance.date)} : ${sheetDecimalFormat.format(settingsFinance.sum)} ${state.currency}"
        }
        ExtraFunctionsBottomSheet(
            title = sheetTitle,
            onEdit = {
                vm.onAction(FinanceDetailsActions.CloseFinanceSheet)
                onEditFinanceClick(settingsFinance)
            },
            onDelete = {
                vm.onAction(FinanceDetailsActions.DeleteFinance(finance = settingsFinance))
                vm.onAction(FinanceDetailsActions.CloseFinanceSheet)
            },
            onDismiss = { vm.onAction(FinanceDetailsActions.CloseFinanceSheet) }
        )
    }
}
