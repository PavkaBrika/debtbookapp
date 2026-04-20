package com.breckneck.debtbook.goal.main

import com.breckneck.debtbook.goal.main.model.GoalUi
import com.breckneck.deptbook.domain.util.ListState

data class GoalsState(
    val goalList: List<GoalUi>,
    val listState: ListState,
    val addDepositPopup: AddDepositPopup = AddDepositPopup.initial(),
) {
    companion object {
        fun initial() = GoalsState(
            goalList = emptyList(),
            listState = ListState.LOADING,
        )
    }
}

data class AddDepositPopup(
    val isVisible: Boolean,
    val selectedGoalId: Int?,
    val sumText: String,
    val inputError: DepositInputError?,
) {
    companion object {
        fun initial() = AddDepositPopup(
            isVisible = false,
            selectedGoalId = null,
            sumText = "",
            inputError = null,
        )
    }

    enum class DepositInputError {
        EMPTY,
        ZERO_OR_INVALID,
    }
}
