package com.breckneck.deptbook.domain.usecase.Debt

import com.breckneck.deptbook.domain.model.DebtDomain
import java.text.DecimalFormat

class GetDebtShareString {

    fun execute(debtList: List<DebtDomain>, name: String, currency: String, sum: Double): String {
        val decimalFormat = DecimalFormat("###,###,###.##")
        val stringBuilder = StringBuilder("$name: ${decimalFormat.format(sum)} $currency\n\n")
        for (debt in debtList) {
            if (debt.info == null)
                stringBuilder.append("${debt.date}: ${decimalFormat.format(debt.sum)} ${currency}\n")
            else
                stringBuilder.append("${debt.date}: ${decimalFormat.format(debt.sum)} $currency (${debt.info})\n")
        }
        return stringBuilder.toString()
    }
}