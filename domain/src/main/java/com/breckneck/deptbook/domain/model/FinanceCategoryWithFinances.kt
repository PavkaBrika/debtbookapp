package com.breckneck.deptbook.domain.model

data class FinanceCategoryWithFinances(
    val financeCategory: FinanceCategory,
    var categorySum: Double = 0.0,
    var categoryPercentage: Int = 0,
    val financeList: MutableList<Finance>
)