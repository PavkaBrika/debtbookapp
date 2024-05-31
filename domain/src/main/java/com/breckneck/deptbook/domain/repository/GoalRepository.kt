package com.breckneck.deptbook.domain.repository

import com.breckneck.deptbook.domain.model.Goal

interface GoalRepository {

    fun getAllGoals(): List<Goal>

    fun setGoal(goal: Goal)

    fun updateGoal(goal: Goal)

    fun deleteGoal(goal: Goal)

    fun replaceAllGoals(goalList: List<Goal>)

}