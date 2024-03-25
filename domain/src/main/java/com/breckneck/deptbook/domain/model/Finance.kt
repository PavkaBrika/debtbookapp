package com.breckneck.deptbook.domain.model

data class Finance(
    var id: Int = 0,
    var name: String,
    var sum: Double,
    var isRevenue: Boolean,
    var info: String?
)
