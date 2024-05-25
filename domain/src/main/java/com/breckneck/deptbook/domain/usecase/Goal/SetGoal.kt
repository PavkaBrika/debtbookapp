package com.breckneck.deptbook.domain.usecase.Goal

import com.breckneck.deptbook.domain.model.Goal
import com.breckneck.deptbook.domain.repository.GoalRepository

class SetGoal(private val goalRepository: GoalRepository) {

    fun execute(goal: Goal) {
        goalRepository.setGoal(goal = goal)
    }
}