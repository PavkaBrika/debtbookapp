package com.breckneck.deptbook.domain.model

import com.breckneck.deptbook.domain.util.FinanceCategoryState

data class FinanceCategory(
    var id: Int = 0,
    var name: String,
    var state: FinanceCategoryState,
    var color: String,
    var image: Int,
    var isChecked: Boolean = false
)
