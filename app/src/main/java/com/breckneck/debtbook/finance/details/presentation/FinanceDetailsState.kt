package com.breckneck.debtbook.finance.details.presentation

import com.breckneck.debtbook.common.empty
import com.breckneck.deptbook.domain.model.Finance
import com.breckneck.deptbook.domain.util.ListState

data class FinanceDetailsState(
    val financeList: List<Finance>,
    val financeListState: ListState,
    val bottomSheet: FinanceDetailsBottomSheetState,
    val categoryId: Int?,
    val categoryName: String,
    val currency: String,
    val isExpenses: Boolean,
) {
    companion object {
        fun initial() = FinanceDetailsState(
            financeList = emptyList(),
            financeListState = ListState.LOADING,
            bottomSheet = FinanceDetailsBottomSheetState.initial(),
            categoryId = null,
            categoryName = String.empty,
            currency = String.empty,
            isExpenses = false,
        )
    }
}

data class FinanceDetailsBottomSheetState(
    val isOpened: Boolean,
    val finance: Finance?,
    val title: String,
) {
    companion object {
        fun initial() = FinanceDetailsBottomSheetState(
            isOpened = false,
            finance = null,
            title = String.empty,
        )
    }
}