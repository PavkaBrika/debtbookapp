package com.breckneck.debtbook.goal.create

sealed interface CreateGoalsSideEffect {
    data object NavigateBack : CreateGoalsSideEffect
    data object LaunchImagePicker : CreateGoalsSideEffect
    data object ShowDatePicker : CreateGoalsSideEffect
}
