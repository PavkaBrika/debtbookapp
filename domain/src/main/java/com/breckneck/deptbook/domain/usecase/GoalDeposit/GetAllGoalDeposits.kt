package com.breckneck.deptbook.domain.usecase.GoalDeposit

import com.breckneck.deptbook.domain.model.GoalDeposit
import com.breckneck.deptbook.domain.repository.GoalDepositRepository

class GetAllGoalDeposits(private val goalDepositRepository: GoalDepositRepository) {

    fun execute(): List<GoalDeposit> {
        return goalDepositRepository.getAllGoalDeposits()
    }
}