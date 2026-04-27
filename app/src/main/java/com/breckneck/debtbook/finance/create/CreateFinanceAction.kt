package com.breckneck.debtbook.finance.create

import com.breckneck.deptbook.domain.model.FinanceCategory
import com.breckneck.deptbook.domain.util.FinanceCategoryState
import java.util.Date

sealed interface CreateFinanceAction {
    data class SumChanged(val value: String) : CreateFinanceAction
    data class InfoChanged(val value: String) : CreateFinanceAction
    data class CategoryClick(val category: FinanceCategory) : CreateFinanceAction
    data class CategoryLongClick(val category: FinanceCategory) : CreateFinanceAction
    data object SwitchCategoryState : CreateFinanceAction
    data object DateClick : CreateFinanceAction
    data class DateSelected(val date: Date) : CreateFinanceAction
    data object DismissDatePicker : CreateFinanceAction
    data object SaveClick : CreateFinanceAction
    data object ConfirmDeleteCategory : CreateFinanceAction
    data object DismissDeleteDialog : CreateFinanceAction
    data object AddCategoryClick : CreateFinanceAction
    data class RefreshCategoriesAfterAdd(
        val newState: FinanceCategoryState,
        val wasModified: Boolean,
    ) : CreateFinanceAction
}
