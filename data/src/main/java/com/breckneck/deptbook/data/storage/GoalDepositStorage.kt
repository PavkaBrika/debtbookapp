package com.breckneck.deptbook.data.storage

import entity.GoalDepositData

interface GoalDepositStorage {

    fun setGoalDepositData(goalDepositData: GoalDepositData)

    fun getGoalDepositsDataByGoalId(goalId: Int): List<GoalDepositData>

    fun deleteGoalDepositsDataByGoalId(goalId: Int)

    fun replaceAllGoalDepositData(goalDepositDataList: List<GoalDepositData>)

    fun getAllGoalDepositsData(): List<GoalDepositData>
}