package com.breckneck.deptbook.domain.util

sealed class HumanFilter {
    object AllHumans: HumanFilter()
    object PositiveHumans : HumanFilter()
    object NegativeHumans : HumanFilter()
}

