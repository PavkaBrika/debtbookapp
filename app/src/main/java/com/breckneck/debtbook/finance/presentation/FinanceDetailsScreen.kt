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
    categoryName: String,
    isExpenses: Boolean,
    currency: String,
    onBackClick: () -> Unit,
    onEditFinanceClick: (Finance) -> Unit
) {
    val context = LocalContext.current

    val state by vm.collectAsState()

    val localizedCategoryName = remember(categoryName, isExpenses) {
        GetFinanceCategoryNameInLocalLanguage().execute(
            financeName = categoryName,
            state = if (isExpenses) FinanceCategoryState.EXPENSE else FinanceCategoryState.INCOME,
            context = context
        )
    }
    val subtitle = if (isExpenses) stringResource(R.string.expenses) else stringResource(R.string.revenues)

    val sheetSdf = remember { SimpleDateFormat("d MMM yyyy", Locale.getDefault()) }
    val sheetDecimalFormat = remember {
        val symbols = DecimalFormatSymbols().apply { groupingSeparator = ' ' }
        DecimalFormat("###,###,###.##", symbols)
    }

    FinanceDetailsContent(
        title = localizedCategoryName,
        subtitle = subtitle,
        isExpenses = isExpenses,
        currency = currency,
        financeList = state.financeList,
        financeListState = state.financeListState,
        onBackClick = onBackClick,
        onFinanceClick = { finance ->
            vm.onAction(FinanceDetailsActions.OpenFinanceSheet(finance = finance))
        }
    )

    if (state.isSettingsDialogOpened && state.settingsFinance != null) {
        val settingsFinance = state.settingsFinance!!
        val sheetTitle = remember(settingsFinance) {
            "${sheetSdf.format(settingsFinance.date)} : ${sheetDecimalFormat.format(settingsFinance.sum)} $currency"
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
