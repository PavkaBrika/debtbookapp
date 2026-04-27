package com.breckneck.debtbook.finance.create

import com.breckneck.debtbook.common.empty
import com.breckneck.deptbook.domain.model.Finance
import com.breckneck.deptbook.domain.model.FinanceCategory
import com.breckneck.deptbook.domain.util.CreateFragmentState
import com.breckneck.deptbook.domain.util.FinanceCategoryState
import java.util.Date

enum class SumError { EMPTY, ZERO, INVALID }

data class DeleteCategoryDialogState(
    val isVisible: Boolean,
    val category: FinanceCategory?,
) {
    companion object {
        fun initial() = DeleteCategoryDialogState(isVisible = false, category = null)
    }
}

data class CreateFinanceState(
    val createFragmentState: CreateFragmentState,
    val financeCategoryState: FinanceCategoryState,
    val financeCategoryList: List<FinanceCategory>,
    val checkedCategoryId: Int?,
    val sum: String,
    val sumError: SumError?,
    val info: String,
    val dateFormatted: String,
    val date: Date,
    val dayInMillis: Long,
    val currency: String,
    val currencyDisplayName: String,
    val financeEdit: Finance?,
    val isDatePickerVisible: Boolean,
    val deleteCategoryDialog: DeleteCategoryDialogState,
    val categoryError: Boolean,
) {
    companion object {
        fun initial() = CreateFinanceState(
            createFragmentState = CreateFragmentState.CREATE,
            financeCategoryState = FinanceCategoryState.EXPENSE,
            financeCategoryList = emptyList(),
            checkedCategoryId = null,
            sum = String.empty,
            sumError = null,
            info = String.empty,
            dateFormatted = String.empty,
            date = Date(),
            dayInMillis = System.currentTimeMillis(),
            currency = String.empty,
            currencyDisplayName = String.empty,
            financeEdit = null,
            isDatePickerVisible = false,
            deleteCategoryDialog = DeleteCategoryDialogState.initial(),
            categoryError = false,
        )
    }
}
