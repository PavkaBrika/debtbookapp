package com.breckneck.deptbook.domain.repository

import com.breckneck.deptbook.domain.model.GoalDeposit

interface GoalDepositRepository {

    fun setGoalDeposit(goalDeposit: GoalDeposit)

    fun getGoalDepositsByGoalId(goalId: Int): List<GoalDeposit>

    fun deleteGoalDepositsByGoalId(goalId: Int)

    fun replaceAllGoalDeposits(goalDepositList: List<GoalDeposit>)
}