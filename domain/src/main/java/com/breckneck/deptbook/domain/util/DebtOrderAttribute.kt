package com.breckneck.deptbook.domain.util

sealed class DebtOrderAttribute {
    object Date: DebtOrderAttribute()
    object Sum: DebtOrderAttribute()
}