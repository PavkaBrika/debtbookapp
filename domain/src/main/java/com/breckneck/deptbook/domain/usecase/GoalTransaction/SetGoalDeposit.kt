package com.breckneck.deptbook.domain.usecase.GoalTransaction

import com.breckneck.deptbook.domain.model.GoalDeposit
import com.breckneck.deptbook.domain.repository.GoalDepositRepository

class SetGoalDeposit(private val goalDepositRepository: GoalDepositRepository) {

    fun execute(goalDeposit: GoalDeposit) {
        goalDepositRepository.setGoalDeposit(goalDeposit = goalDeposit)
    }
}