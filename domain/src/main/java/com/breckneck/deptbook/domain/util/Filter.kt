package com.breckneck.deptbook.domain.util

sealed class Filter {
    object All: Filter()
    object Positive : Filter()
    object Negative : Filter()
}

