package com.breckneck.debtbook.goal.main.mapper

import android.content.Context
import com.breckneck.debtbook.R
import com.breckneck.debtbook.common.format
import com.breckneck.debtbook.common.toDMYFormat
import com.breckneck.debtbook.goal.main.model.GoalUi
import com.breckneck.deptbook.domain.model.Goal
import java.io.File
import java.util.Date
import java.util.concurrent.TimeUnit

fun Goal.toUi(context: Context): GoalUi {
    val isReached = sum - savedSum <= 0

    val dateChipText: String?
    val isOverdue: Boolean

    when {
        isReached -> {
            val diffInDays = TimeUnit.DAYS
                .convert(Date().time - creationDate.time, TimeUnit.MILLISECONDS)
                .coerceAtLeast(1L)

            dateChipText = if (diffInDays == 1L) {
                context.getString(R.string.achieved_in_day, diffInDays)
            } else {
                context.getString(R.string.achieved_in_days, diffInDays)
            }

            isOverdue = false
        }

        goalDate != null -> {
            val date = goalDate!!

            isOverdue = date.before(Date())

            dateChipText = if (isOverdue) {
                context.getString(R.string.overdue)
            } else {
                date.toDMYFormat()
            }
        }

        else -> {
            dateChipText = null
            isOverdue = false
        }
    }

    return GoalUi(
        goalId = id,
        name = name,
        photoPath = photoPath,
        hasPhoto = photoPath != null && File(photoPath!!).exists(),
        savedAmount = savedSum.format(),
        totalAmount = sum.format(),
        currency = currency,
        isReached = isReached,
        remainingAmount = if (!isReached) (sum - savedSum).format() else null,
        date = dateChipText,
        isOverdue = isOverdue,
    )
}
