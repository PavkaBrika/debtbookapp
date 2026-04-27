package com.breckneck.debtbook.goal.create

import com.breckneck.debtbook.goal.create.model.NameError
import com.breckneck.debtbook.goal.create.model.SavedSumError
import com.breckneck.debtbook.goal.create.model.SumError

/**
 * Pure validation for create/edit goal form. Used by [CreateGoalsViewModel] and unit-tested directly.
 */
object CreateGoalsFormValidation {

    fun validateName(name: String): NameError? =
        if (name.isEmpty()) NameError.EMPTY else null

    fun validateSum(sumText: String): Pair<Double?, SumError?> {
        val sumDouble = sumText.toDoubleOrNull()
        val error = when {
            sumText.isEmpty() || sumDouble == null -> SumError.INVALID
            sumDouble < 0 -> SumError.NEGATIVE
            sumDouble == 0.0 -> SumError.ZERO
            else -> null
        }
        return Pair(sumDouble, error)
    }

    fun validateSavedSum(
        savedSumText: String,
        sumDouble: Double?,
    ): Pair<Double?, SavedSumError?> {
        if (savedSumText.isEmpty()) return Pair(0.0, null)
        val parsed = savedSumText.toDoubleOrNull() ?: return Pair(0.0, SavedSumError.INVALID)
        if (parsed < 0) return Pair(0.0, SavedSumError.NEGATIVE)
        if (sumDouble != null && parsed >= sumDouble) {
            return Pair(0.0, SavedSumError.GREATER_THAN_SUM)
        }
        return Pair(parsed, null)
    }
}
