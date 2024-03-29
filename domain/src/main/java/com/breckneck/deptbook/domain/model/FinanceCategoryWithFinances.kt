package com.breckneck.deptbook.domain.model

data class FinanceCategoryWithFinances(
    val financeCategory: FinanceCategory,
    val financeList: MutableList<Finance>
)