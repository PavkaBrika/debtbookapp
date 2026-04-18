package com.breckneck.debtbook.goal.main

import com.breckneck.deptbook.domain.model.Goal

sealed interface GoalsAction {
    data object AddGoalClick : GoalsAction
    data class GoalClick(val goal: Goal) : GoalsAction
    data class AddGoalDeposit(val goal: Goal, val sum: Double) : GoalsAction
    data class DeleteGoal(val goal: Goal) : GoalsAction
    data class RefreshAfterNavigation(val wasModified: Boolean) : GoalsAction
}