package com.breckneck.deptbook.domain.model

import java.util.Date

data class GoalDeposit(
    var id: Int = 0,
    var sum: Double,
    var date: Date,
    var goalId: Int
)