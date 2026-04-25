package com.breckneck.debtbook.goal.create

import android.net.Uri
import com.breckneck.debtbook.common.empty
import java.util.Date

data class CreateGoalsState(
    val name: String,
    val nameError: NameError?,
    val sum: String,
    val sumError: SumError?,
    val savedSum: String,
    val savedSumError: SavedSumError?,
    val currency: String,
    val currencyDisplayName: String,
    val currencyNames: List<String>,
    val selectedCurrencyIndex: Int,
    val isCurrencySheetVisible: Boolean,
    val isDatePickerVisible: Boolean,
    val goalDate: Date?,
    val goalDateFormatted: String?,
    val imageUri: Uri?,
    val imagePath: String?,
    val hasImage: Boolean,
    val isEditMode: Boolean,
) {
    companion object {
        fun initial() = CreateGoalsState(
            name = String.empty,
            nameError = null,
            sum = String.empty,
            sumError = null,
            savedSum = String.empty,
            savedSumError = null,
            currency = String.empty,
            currencyDisplayName = String.empty,
            currencyNames = emptyList(),
            selectedCurrencyIndex = 0,
            isCurrencySheetVisible = false,
            isDatePickerVisible = false,
            goalDate = null,
            goalDateFormatted = null,
            imageUri = null,
            imagePath = null,
            hasImage = false,
            isEditMode = false,
        )
    }
}
