package com.breckneck.deptbook.domain.util

sealed class HumanFilters {
    object AllHumans: HumanFilters()
    object PositiveHumans : HumanFilters()
    object NegativeHumans : HumanFilters()
}

