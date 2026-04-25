package com.breckneck.debtbook.goal.create.model

import android.net.Uri
import com.breckneck.debtbook.common.empty
import java.util.Date

data class CreateGoalUi(
    val name: String = String.empty,
    val sum: String = String.empty,
    val savedSum: String = String.empty,
    val currency: String = String.empty,
    val currencyDisplayName: String = String.empty,
    val goalDate: Date? = null,
    val imageUri: Uri? = null,
    val imagePath: String? = null,
) {
    val hasImage: Boolean
        get() = imageUri != null || imagePath != null
}
