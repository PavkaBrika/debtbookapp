package com.breckneck.debtbook.finance.create

import com.breckneck.deptbook.domain.util.FinanceCategoryState

sealed interface CreateFinanceSideEffect {
    data class NavigateBack(val saved: Boolean = false) : CreateFinanceSideEffect
    data class NavigateToAddCategory(val state: FinanceCategoryState) : CreateFinanceSideEffect
    data object Vibrate : CreateFinanceSideEffect
}
