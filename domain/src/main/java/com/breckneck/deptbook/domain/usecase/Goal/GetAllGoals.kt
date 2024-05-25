package com.breckneck.deptbook.domain.usecase.Goal

import com.breckneck.deptbook.domain.model.Goal
import com.breckneck.deptbook.domain.repository.GoalRepository

class GetAllGoals(private val goalRepository: GoalRepository) {

    fun execute(): List<Goal> {
        return goalRepository.getAllGoals()
    }
}