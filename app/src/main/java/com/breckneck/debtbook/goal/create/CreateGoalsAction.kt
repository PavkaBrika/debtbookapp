package com.breckneck.debtbook.goal.create

import android.net.Uri
import java.util.Date

sealed interface CreateGoalsAction {
    data class NameChanged(val value: String) : CreateGoalsAction
    data class SumChanged(val value: String) : CreateGoalsAction
    data class SavedSumChanged(val value: String) : CreateGoalsAction
    data object CurrencyClick : CreateGoalsAction
    data class CurrencySelected(val index: Int) : CreateGoalsAction
    data object DismissCurrencySheet : CreateGoalsAction
    data object DateClick : CreateGoalsAction
    data object DismissDatePicker : CreateGoalsAction
    data class DateSelected(val date: Date) : CreateGoalsAction
    data class ImagePicked(val uri: Uri) : CreateGoalsAction
    data object DeleteImage : CreateGoalsAction
    data object PhotoCardClick : CreateGoalsAction
    data object SaveClick : CreateGoalsAction
    data object BackClick : CreateGoalsAction
}
