package com.breckneck.deptbook.domain.util

sealed class HumanListState(
    val needToSetFilter: Boolean? = null,
    val needToSetOrder: Boolean? = null
) {
    data object Loading : HumanListState()
    class Received(needToSetFilter: Boolean, needToSetOrder: Boolean) :
        HumanListState(needToSetFilter, needToSetOrder)
    data object Sorted : HumanListState()
}