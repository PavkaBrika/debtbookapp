package com.breckneck.debtbook.goal.create.mapper

import com.breckneck.debtbook.common.empty
import com.breckneck.debtbook.common.format
import com.breckneck.debtbook.goal.create.model.CreateGoalUi
import com.breckneck.deptbook.domain.model.Goal
import java.util.Calendar

fun Goal.toCreateGoalUi(currencyDisplayName: String): CreateGoalUi =
    CreateGoalUi(
        name = name,
        sum = sum.format(),
        savedSum = if (savedSum != 0.0) savedSum.format() else String.empty,
        currency = currency,
        currencyDisplayName = currencyDisplayName,
        goalDate = goalDate,
        imagePath = photoPath,
    )

fun CreateGoalUi.toDomain(
    sum: Double,
    savedSum: Double,
    photoPath: String?,
    originalGoal: Goal? = null,
): Goal =
    Goal(
        id = originalGoal?.id ?: 0,
        name = name.trim(),
        sum = sum,
        savedSum = savedSum,
        currency = currency,
        photoPath = photoPath,
        creationDate = originalGoal?.creationDate ?: Calendar.getInstance().time,
        goalDate = goalDate,
    )
