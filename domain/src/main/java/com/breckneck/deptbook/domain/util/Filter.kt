package com.breckneck.deptbook.domain.util

sealed class Filter {
    data object All: Filter()
    data object Positive : Filter()
    data object Negative : Filter()
}

