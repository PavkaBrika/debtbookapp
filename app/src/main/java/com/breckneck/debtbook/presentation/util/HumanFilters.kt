package com.breckneck.debtbook.presentation.util

sealed class HumanFilters {
    object AllHumans: HumanFilters()
    object PositiveHumans : HumanFilters()
    object NegativeHumans : HumanFilters()
}

