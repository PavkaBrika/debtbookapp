package com.breckneck.deptbook.domain.usecase.Goal

import com.breckneck.deptbook.domain.model.Goal
import com.breckneck.deptbook.domain.repository.GoalRepository

class ReplaceAllGoals(private val goalRepository: GoalRepository) {

    fun execute(goalList: List<Goal>) {
        goalRepository.replaceAllGoals(goalList = goalList)
    }
}