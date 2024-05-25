package com.breckneck.deptbook.data.storage

import entity.GoalData

interface GoalStorage {

    fun getAllGoals(): List<GoalData>

    fun setGoal(goal: GoalData)
}