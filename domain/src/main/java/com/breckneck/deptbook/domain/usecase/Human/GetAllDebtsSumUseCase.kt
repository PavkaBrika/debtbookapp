package com.breckneck.deptbook.domain.usecase.Human

import com.breckneck.deptbook.domain.repository.HumanRepository
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import kotlin.math.abs

class GetAllDebtsSumUseCase(val humanRepository: HumanRepository) {

    val decimalFormat = DecimalFormat("###,###,###.##")
    val customSymbol: DecimalFormatSymbols = DecimalFormatSymbols()

    fun execute(sign: String, firstCurrency: String, secondCurrency: String): String {
        customSymbol.groupingSeparator = ' '
        decimalFormat.decimalFormatSymbols = customSymbol
        val debtsInFirstCurrency = humanRepository.getAllDebtsSum(currency = firstCurrency)
        val debtsInSecondCurrency = humanRepository.getAllDebtsSum(currency = secondCurrency)
        var positiveNumbers = 0.0
        var positiveText = ""
        var negativeText = ""
        var negativeNumbers = 0.0
        positiveNumbers = 0.0
        negativeNumbers = 0.0
        if (debtsInFirstCurrency.isNotEmpty()) {
            for (i in debtsInFirstCurrency.indices) {
                if (debtsInFirstCurrency[i] > 0) {
                    positiveNumbers += debtsInFirstCurrency[i]
                }
                if (debtsInFirstCurrency[i] < 0) {
                    negativeNumbers += abs(debtsInFirstCurrency[i])
                }
            }
            if (positiveNumbers != 0.0)
                if (positiveText.isEmpty())
                    positiveText = "+${decimalFormat.format(positiveNumbers)} $firstCurrency"
                else
                    positiveText = "$positiveText\n+${decimalFormat.format(positiveNumbers)} $firstCurrency"
            if (negativeNumbers != 0.0)
                if (negativeText.isEmpty())
                    negativeText = "-${decimalFormat.format(negativeNumbers)} $firstCurrency"
                else
                    negativeText = "$negativeText\n-${decimalFormat.format(negativeNumbers)} $firstCurrency"
        }
        positiveNumbers = 0.0
        negativeNumbers = 0.0
        if (debtsInSecondCurrency.isNotEmpty()) {
            for (i in debtsInSecondCurrency.indices) {
                if (debtsInSecondCurrency[i] > 0) {
                    positiveNumbers += debtsInSecondCurrency[i]
                }
                if (debtsInSecondCurrency[i] < 0) {
                    negativeNumbers += abs(debtsInSecondCurrency[i])
                }
            }
            if (positiveNumbers != 0.0)
                if (positiveText.isEmpty())
                    positiveText = "+${decimalFormat.format(positiveNumbers)} $secondCurrency"
                else
                    positiveText = "$positiveText\n+${decimalFormat.format(positiveNumbers)} $secondCurrency"
            if (negativeNumbers != 0.0)
                if (negativeText.isEmpty())
                    negativeText = "-${decimalFormat.format(negativeNumbers)} $secondCurrency"
                else
                    negativeText = "$negativeText\n-${decimalFormat.format(negativeNumbers)} $secondCurrency"
        }
        if (sign == "positive")
            return positiveText
        else
            return negativeText
    }
}