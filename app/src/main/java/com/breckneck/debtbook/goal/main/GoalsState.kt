package com.breckneck.debtbook.goal.main

import com.breckneck.deptbook.domain.model.Goal
import com.breckneck.deptbook.domain.util.ListState

data class GoalsState(
    val goalList: List<Goal>,
    val listState: ListState,
) {
    companion object {
        fun initial() = GoalsState(
            goalList = emptyList(),
            listState = ListState.LOADING,
        )
    }
}

sealed interface GoalsSideEffect {
    data object NavigateToAddGoal : GoalsSideEffect
    data class NavigateToGoalDetails(val goal: Goal) : GoalsSideEffect
}
