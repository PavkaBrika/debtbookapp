package com.breckneck.deptbook.domain.util

sealed class DebtLogicListState(
    val needToSetFilter: Boolean? = null,
    val needToSetOrder: Boolean? = null
) {
    data object Loading : DebtLogicListState()
    class Received(needToSetFilter: Boolean, needToSetOrder: Boolean) :
        DebtLogicListState(needToSetFilter, needToSetOrder)
    data object Sorted : DebtLogicListState()
}