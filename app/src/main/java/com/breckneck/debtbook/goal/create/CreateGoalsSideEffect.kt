package com.breckneck.debtbook.goal.create

import com.breckneck.deptbook.domain.model.Goal

sealed interface CreateGoalsSideEffect {
    data class NavigateBack(
        val editedGoal: Goal? = null,
        val saved: Boolean = false,
    ) : CreateGoalsSideEffect
    data object LaunchImagePicker : CreateGoalsSideEffect
    data object ShowDatePicker : CreateGoalsSideEffect
}
