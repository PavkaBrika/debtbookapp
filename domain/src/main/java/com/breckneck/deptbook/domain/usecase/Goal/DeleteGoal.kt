package com.breckneck.deptbook.domain.usecase.Goal

import com.breckneck.deptbook.domain.model.Goal
import com.breckneck.deptbook.domain.repository.GoalRepository

class DeleteGoal(private val goalRepository: GoalRepository) {

    fun execute(goal: Goal) {
        goalRepository.deleteGoal(goal = goal)
    }
}