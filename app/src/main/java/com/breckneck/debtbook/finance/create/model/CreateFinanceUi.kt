package com.breckneck.debtbook.finance.create.model

import com.breckneck.debtbook.common.empty
import java.util.Date

data class CreateFinanceUi(
    val sum: String = String.empty,
    val info: String = String.empty,
    val date: Date = Date(),
    val dayInMillis: Long = System.currentTimeMillis(),
    val currency: String = String.empty,
    val currencyDisplayName: String = String.empty,
)
