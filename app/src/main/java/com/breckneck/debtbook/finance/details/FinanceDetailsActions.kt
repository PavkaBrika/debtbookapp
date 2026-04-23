package com.breckneck.debtbook.finance.details

import com.breckneck.deptbook.domain.model.Finance

sealed interface FinanceDetailsActions {
    data class OpenFinanceSheet(val finance: Finance) : FinanceDetailsActions
    data object CloseFinanceSheet : FinanceDetailsActions
    data object DeleteFinance : FinanceDetailsActions
    data object EditFinanceClick : FinanceDetailsActions
    data class RefreshListAfterEdit(val wasModified: Boolean) : FinanceDetailsActions
}
