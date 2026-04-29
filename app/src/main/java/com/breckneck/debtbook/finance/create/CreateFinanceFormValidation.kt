package com.breckneck.debtbook.finance.create

import com.breckneck.debtbook.finance.create.model.SumError

object CreateFinanceFormValidation {

    fun validateSum(text: String): Pair<Double?, SumError?> {
        if (text.isEmpty()) return null to SumError.EMPTY
        return try {
            val value = text.toDouble()
            if (value == 0.0) null to SumError.ZERO
            else value to null
        } catch (e: NumberFormatException) {
            null to SumError.INVALID
        }
    }
}
