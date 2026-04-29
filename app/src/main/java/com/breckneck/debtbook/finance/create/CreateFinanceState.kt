package com.breckneck.debtbook.finance.create

import com.breckneck.debtbook.finance.create.model.CreateFinanceUi
import com.breckneck.debtbook.finance.create.model.SumError
import com.breckneck.deptbook.domain.model.Finance
import com.breckneck.deptbook.domain.model.FinanceCategory
import com.breckneck.deptbook.domain.util.CreateFragmentState
import com.breckneck.deptbook.domain.util.FinanceCategoryState

data class DeleteCategoryDialogState(
    val isVisible: Boolean,
    val category: FinanceCategory?,
) {
    companion object {
        fun initial() = DeleteCategoryDialogState(isVisible = false, category = null)
    }
}

data class CreateFinanceState(
    val form: CreateFinanceUi,
    val createFragmentState: CreateFragmentState,
    val financeCategoryState: FinanceCategoryState,
    val financeCategoryList: List<FinanceCategory>,
    val checkedCategoryId: Int?,
    val sumError: SumError?,
    val financeEdit: Finance?,
    val isDatePickerVisible: Boolean,
    val deleteCategoryDialog: DeleteCategoryDialogState,
    val categoryError: Boolean,
) {
    companion object {
        fun initial() = CreateFinanceState(
            form = CreateFinanceUi(),
            createFragmentState = CreateFragmentState.CREATE,
            financeCategoryState = FinanceCategoryState.EXPENSE,
            financeCategoryList = emptyList(),
            checkedCategoryId = null,
            sumError = null,
            financeEdit = null,
            isDatePickerVisible = false,
            deleteCategoryDialog = DeleteCategoryDialogState.initial(),
            categoryError = false,
        )
    }
}
