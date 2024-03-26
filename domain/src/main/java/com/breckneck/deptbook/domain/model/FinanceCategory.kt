package com.breckneck.deptbook.domain.model

data class FinanceCategory(
    var id: Int = 0,
    var name: String,
    var color: String,
    var image: Int,
    var isChecked: Boolean = false
)
