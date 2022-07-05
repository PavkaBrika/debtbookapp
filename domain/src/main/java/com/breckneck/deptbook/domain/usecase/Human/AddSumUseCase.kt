package com.breckneck.deptbook.domain.usecase.Human

import com.breckneck.deptbook.domain.repository.HumanRepository

class AddSumUseCase(val humanRepository: HumanRepository) {

    fun execute(humanId: Int, sum: Double) {
        humanRepository.addSum(humanId = humanId, sum = sum)
    }
}