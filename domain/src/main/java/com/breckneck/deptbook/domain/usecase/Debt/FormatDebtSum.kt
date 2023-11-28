package com.breckneck.deptbook.domain.usecase.Debt

import java.text.DecimalFormat

class FormatDebtSum {

    fun execute(sum:Double): String {
        val decimalFormat = DecimalFormat("###,###,###.##")
        return decimalFormat.format(sum)
    }
}