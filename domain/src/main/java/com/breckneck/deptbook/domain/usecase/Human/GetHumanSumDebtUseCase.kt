package com.breckneck.deptbook.domain.usecase.Human

import com.breckneck.deptbook.domain.repository.HumanRepository

class GetHumanSumDebtUseCase(val humanRepository: HumanRepository) {



    fun execute(humanId: Int): Double {
        return humanRepository.getHumanSumDebt(humanId = humanId)
    }
}