package com.breckneck.deptbook.domain.usecase.Goal

import com.breckneck.deptbook.domain.model.Goal
import com.breckneck.deptbook.domain.repository.GoalRepository

class UpdateGoal(private val goalRepository: GoalRepository) {

    fun execute(goal: Goal) {
        goalRepository.updateGoal(goal = goal)
    }
}