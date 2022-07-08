package com.breckneck.deptbook.domain.usecase.Human

import com.breckneck.deptbook.domain.repository.HumanRepository
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class GetHumanSumDebtUseCase(val humanRepository: HumanRepository) {



    fun execute(humanId: Int): Double {
        return humanRepository.getHumanSumDebt(humanId = humanId)
    }
}