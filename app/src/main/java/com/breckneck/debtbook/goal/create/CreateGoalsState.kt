package com.breckneck.debtbook.goal.create

import android.net.Uri
import java.util.Date

data class CreateGoalsState(
    val name: String,
    val nameError: String?,
    val sum: String,
    val sumError: String?,
    val savedSum: String,
    val savedSumError: String?,
    val currency: String,
    val currencyDisplayName: String,
    val selectedCurrencyIndex: Int,
    val isCurrencySheetVisible: Boolean,
    val goalDate: Date?,
    val goalDateFormatted: String?,
    val imageUri: Uri?,
    val imagePath: String?,
    val hasImage: Boolean,
    val isEditMode: Boolean,
    val title: String,
) {
    companion object {
        fun initial() = CreateGoalsState(
            name = "",
            nameError = null,
            sum = "",
            sumError = null,
            savedSum = "",
            savedSumError = null,
            currency = "",
            currencyDisplayName = "",
            selectedCurrencyIndex = 0,
            isCurrencySheetVisible = false,
            goalDate = null,
            goalDateFormatted = null,
            imageUri = null,
            imagePath = null,
            hasImage = false,
            isEditMode = false,
            title = "",
        )
    }
}
