package com.breckneck.deptbook.domain.usecase.GoalDeposit

import com.breckneck.deptbook.domain.model.GoalDeposit
import com.breckneck.deptbook.domain.repository.GoalDepositRepository

class ReplaceAllGoalsDeposits(private val goalDepositRepository: GoalDepositRepository) {

    fun execute(goalDepositList: List<GoalDeposit>) {
        goalDepositRepository.replaceAllGoalDeposits(goalDepositList = goalDepositList)
    }
}