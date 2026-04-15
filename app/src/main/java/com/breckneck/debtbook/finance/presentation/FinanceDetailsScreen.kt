package com.breckneck.debtbook.finance.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.breckneck.debtbook.R
import com.breckneck.debtbook.core.ui.components.ExtraFunctionsBottomSheet
import com.breckneck.debtbook.finance.util.GetFinanceCategoryNameInLocalLanguage
import com.breckneck.debtbook.finance.viewmodel.FinanceDetailsViewModel
import com.breckneck.deptbook.domain.model.Finance
import com.breckneck.deptbook.domain.util.FinanceCategoryState
import com.breckneck.deptbook.domain.util.ListState
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
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
        financeList = financeList,
        financeListState = financeListState,
        onBackClick = onBackClick,
        onFinanceClick = { finance ->
            vm.onFinanceSettingsDialogOpen()
            vm.onSetSettingFinance(finance = finance)
        }
    )

    if (isSettingsDialogOpened && settingsFinance != null) {
        val sheetTitle = remember(settingsFinance) {
            "${sheetSdf.format(settingsFinance!!.date)} : ${sheetDecimalFormat.format(settingsFinance!!.sum)} $currency"
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
            onDismiss = { vm.onFinanceSettingsDialogClose() }
        )
    }
}
