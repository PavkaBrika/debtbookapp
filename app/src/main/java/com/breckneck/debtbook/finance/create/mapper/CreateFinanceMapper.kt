package com.breckneck.debtbook.finance.create.mapper

import com.breckneck.debtbook.finance.create.model.CreateFinanceUi
import com.breckneck.deptbook.domain.model.Finance

fun Finance.toCreateFinanceUi(
    currency: String,
    currencyDisplayName: String,
): CreateFinanceUi = CreateFinanceUi(
    sum = formatSumForDisplay(this.sum),
    info = this.info.orEmpty(),
    date = this.date,
    dayInMillis = this.date.time,
    currency = currency,
    currencyDisplayName = currencyDisplayName,
)

private fun formatSumForDisplay(sum: Double): String {
    return if (sum == sum.toLong().toDouble()) sum.toLong().toString()
    else "%.2f".format(sum).trimEnd('0')
}
