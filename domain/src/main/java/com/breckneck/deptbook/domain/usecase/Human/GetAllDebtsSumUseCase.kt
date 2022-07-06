package com.breckneck.deptbook.domain.usecase.Human

import com.breckneck.deptbook.domain.repository.HumanRepository
import kotlin.math.abs

class GetAllDebtsSumUseCase(val humanRepository: HumanRepository) {

    fun execute(sign: String): String {
        val debtsRUB = humanRepository.getAllDebtsSum(currency = "RUB")
        val debtsEUR = humanRepository.getAllDebtsSum(currency = "EUR")
        val debtsUSD = humanRepository.getAllDebtsSum(currency = "USD")
        var positiveNumbers = 0.0
        var positiveText = ""
        var negativeText = ""
        var negativeNumbers = 0.0
        if (debtsRUB.isNotEmpty()) {
            for (i in debtsRUB.indices) {
                if (debtsRUB[i] > 0) {
                    positiveNumbers += debtsRUB[i]
                }
                if (debtsRUB[i] < 0) {
                    negativeNumbers += abs(debtsRUB[i])
                }
            }
            if (positiveNumbers != 0.0)
                positiveText = "+$positiveNumbers RUB"
            if (negativeNumbers != 0.0)
                negativeText = "-$negativeNumbers RUB"
        }
        positiveNumbers = 0.0
        negativeNumbers = 0.0
        if (debtsUSD.isNotEmpty()) {
            for (i in debtsUSD.indices) {
                if (debtsUSD[i] > 0) {
                    positiveNumbers += debtsUSD[i]
                }
                if (debtsUSD[i] < 0) {
                    negativeNumbers += abs(debtsUSD[i])
                }
            }
            if (positiveNumbers != 0.0)
                positiveText = "$positiveText\n+$positiveNumbers USD"
            if (negativeNumbers != 0.0)
                negativeText = "$negativeText\n-$negativeNumbers USD"
        }
        positiveNumbers = 0.0
        negativeNumbers = 0.0
        if (debtsEUR.isNotEmpty()) {
            for (i in debtsEUR.indices) {
                if (debtsEUR[i] > 0) {
                    positiveNumbers += debtsEUR[i]
                }
                if (debtsEUR[i] < 0) {
                    negativeNumbers += abs(debtsEUR[i])
                }
            }
            if (positiveNumbers != 0.0)
                positiveText = "$positiveText\n+$positiveNumbers EUR"
            if (negativeNumbers != 0.0)
                negativeText = "$negativeText\n-$negativeNumbers EUR"
        }
        if (sign == "positive")
            return positiveText
        else
            return negativeText
    }
}