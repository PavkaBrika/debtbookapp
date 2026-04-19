package com.breckneck.debtbook.goal.main

import com.breckneck.debtbook.goal.main.model.GoalUi

sealed interface GoalsAction {
    data object AddGoalClick : GoalsAction
    data class GoalClick(val goalUi: GoalUi) : GoalsAction
    data class ShowAddDepositPopup(val goalUi: GoalUi) : GoalsAction
    data object DismissAddDepositPopup : GoalsAction
    data class AddGoalDeposit(val sum: Double) : GoalsAction
    data class DeleteGoal(val goalUi: GoalUi) : GoalsAction
    data class RefreshAfterNavigation(val wasModified: Boolean) : GoalsAction
}
