package com.breckneck.deptbook.domain.usecase.GoalDeposit

import com.breckneck.deptbook.domain.repository.GoalDepositRepository

class DeleteGoalDepositsByGoalId(private val goalDepositRepository: GoalDepositRepository) {

    fun execute(goalId: Int) {
        goalDepositRepository.deleteGoalDepositsByGoalId(goalId = goalId)
    }
}