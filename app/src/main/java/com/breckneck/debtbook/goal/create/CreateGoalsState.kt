package com.breckneck.debtbook.goal.create

import com.breckneck.debtbook.goal.create.model.CreateGoalUi

data class CreateGoalsState(
    val goal: CreateGoalUi,
    val nameError: NameError?,
    val sumError: SumError?,
    val savedSumError: SavedSumError?,
    val currencyNames: List<String>,
    val selectedCurrencyIndex: Int,
    val isCurrencySheetVisible: Boolean,
    val isDatePickerVisible: Boolean,
    val isEditMode: Boolean,
) {
    companion object {
        fun initial() = CreateGoalsState(
            goal = CreateGoalUi(),
            nameError = null,
            sumError = null,
            savedSumError = null,
            currencyNames = emptyList(),
            selectedCurrencyIndex = 0,
            isCurrencySheetVisible = false,
            isDatePickerVisible = false,
            isEditMode = false,
        )
    }
}
