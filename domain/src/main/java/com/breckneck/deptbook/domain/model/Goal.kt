package com.breckneck.deptbook.domain.model

import java.util.Date

data class Goal(
    val id: Int = 0,
    var name: String,
    var sum: Double,
    var savedSum: Double,
    var creationDate: Date,
    var goalDate: Date
)