package com.breckneck.deptbook.domain.usecase.Debt

import com.breckneck.deptbook.domain.model.DebtDomain
import java.text.DecimalFormat

class GetDebtShareString {

    fun execute(debtList: List<DebtDomain>, name: String, currency: String, sum: Double, addSumInShareText: Boolean): String {
        val decimalFormat = DecimalFormat("###,###,###.##")
        val stringBuilder = StringBuilder("$name: ${decimalFormat.format(sum)} $currency\n\n")
        if (!addSumInShareText) {
            for (debt in debtList) {
                if (debt.info == null)
                    stringBuilder.append("${debt.date}: ${decimalFormat.format(debt.sum)} ${currency}\n")
                else
                    stringBuilder.append("${debt.date}: ${decimalFormat.format(debt.sum)} $currency (${debt.info})\n")
            }
        } else {
            var sumForEachNote = 0.0
            for (debt in debtList) {
                sumForEachNote += debt.sum
                if (debt.info == null)
                    stringBuilder.append("${debt.date}: ${decimalFormat.format(debt.sum)} ${currency} (${decimalFormat.format(sumForEachNote)} ${currency})\n")
                else
                    stringBuilder.append("${debt.date}: ${decimalFormat.format(debt.sum)} $currency (${debt.info}) (${decimalFormat.format(sumForEachNote)})\n")
            }
        }
        return stringBuilder.toString()
    }
}