package com.breckneck.debtbook.finance.viewmodel

import com.breckneck.deptbook.domain.model.Finance

sealed interface FinanceDetailsActions {
    data class OpenFinanceSheet(val finance: Finance) : FinanceDetailsActions
    data object CloseFinanceSheet : FinanceDetailsActions
    data class DeleteFinance(val finance: Finance) : FinanceDetailsActions
    data class RefreshListAfterEdit(val wasModified: Boolean) : FinanceDetailsActions
}
