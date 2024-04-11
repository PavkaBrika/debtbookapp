package com.breckneck.deptbook.domain.model

import java.io.Serializable
import java.util.Date

data class Finance(
    var id: Int = 0,
    var sum: Double,
    var date: Date,
    var info: String?,
    var financeCategoryId: Int
): Serializable
