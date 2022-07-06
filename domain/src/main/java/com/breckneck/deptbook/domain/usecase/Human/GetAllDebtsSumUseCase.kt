package com.breckneck.deptbook.domain.usecase.Human

import com.breckneck.deptbook.domain.repository.HumanRepository
import kotlin.math.abs

class GetAllDebtsSumUseCase(val humanRepository: HumanRepository) {

    fun execute(sign: String): String {
        val debts = humanRepository.getAllDebtsSum()
        var positiveNumbers = 0.0
        var negativeNumbers = 0.0
        for (i in debts.indices) {
            if (debts[i] > 0)
                positiveNumbers += debts[i]
            if (debts[i] < 0)
                negativeNumbers += abs(debts[i])
        }
        if (sign == "positive")
            return positiveNumbers.toString()
        else
            return negativeNumbers.toString()
    }
}