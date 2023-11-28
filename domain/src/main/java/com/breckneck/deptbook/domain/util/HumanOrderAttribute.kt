package com.breckneck.deptbook.domain.util

sealed class HumanOrderAttribute {
    object Date: HumanOrderAttribute()
    object Sum: HumanOrderAttribute()
}