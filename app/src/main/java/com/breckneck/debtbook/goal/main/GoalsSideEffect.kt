package com.breckneck.debtbook.goal.main

import com.breckneck.deptbook.domain.model.Goal

sealed interface GoalsSideEffect {
    data object NavigateToAddGoal : GoalsSideEffect
    data class NavigateToGoalDetails(val goal: Goal) : GoalsSideEffect
}