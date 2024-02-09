package com.breckneck.deptbook.domain.util

sealed class DebtOrderAttribute {
    object CreationDate: DebtOrderAttribute()
    object Date: DebtOrderAttribute()
    object Sum: DebtOrderAttribute()
}