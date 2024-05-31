package com.breckneck.deptbook.data.storage

import entity.GoalData

interface GoalStorage {

    fun getAllGoals(): List<GoalData>

    fun setGoal(goalData: GoalData)

    fun updateGoal(goalData: GoalData)

    fun deleteGoal(goalData: GoalData)

    fun replaceAllGoals(goalList: List<GoalData>)
}