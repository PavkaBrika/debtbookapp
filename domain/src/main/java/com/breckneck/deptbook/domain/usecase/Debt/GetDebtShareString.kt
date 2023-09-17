package com.breckneck.deptbook.domain.usecase.Debt

import com.breckneck.deptbook.domain.model.DebtDomain
import java.text.DecimalFormat

class GetDebtShareString {

    fun execute(debtList: List<DebtDomain>, name: String, currency: String, sum: Double, addSumInShareText: Boolean): String {
        val decimalFormat = DecimalFormat("###,###,###.##")
        val stringBuilder = StringBuilder("$name: ${decimalFormat.format(sum)} $currency\n\n")
        if (!addSumInShareText) {
            for (debt in debtList) {
                var sumString = decimalFormat.format(debt.sum)
                if (!sumString.contains("-"))
                    sumString = "+$sumString"
                if (debt.info == null)
                    stringBuilder.append("${debt.date}: $sumString ${currency}\n")
                else
                    stringBuilder.append("${debt.date}: $sumString $currency (${debt.info})\n")
            }
        } else {
            var sumForEachNote = 0.0
            for (debt in debtList) {
                var sumString = decimalFormat.format(debt.sum)
                if (!sumString.contains("-"))
                    sumString = "+$sumString"
                sumForEachNote += debt.sum
                if (debt.info == null)
                    stringBuilder.append("${debt.date}: $sumString $currency (${decimalFormat.format(sumForEachNote)} ${currency})\n")
                else
                    stringBuilder.append("${debt.date}: $sumString $currency (${debt.info}) (${decimalFormat.format(sumForEachNote)})\n")
            }
        }
        return stringBuilder.toString()
    }
}