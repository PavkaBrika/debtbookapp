package com.breckneck.deptbook.domain.usecase.GoalTransaction

import com.breckneck.deptbook.domain.model.GoalDeposit
import com.breckneck.deptbook.domain.repository.GoalDepositRepository

class GetGoalDepositsByGoalId(private val goalDepositRepository: GoalDepositRepository) {

    fun execute(goalId: Int): List<GoalDeposit> {
        return goalDepositRepository.getGoalDepositsByGoalId(goalId = goalId)
    }
}