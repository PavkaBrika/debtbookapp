package com.breckneck.deptbook.domain.util

sealed class DebtLogicListState(
    val needToSetFilter: Boolean? = null,
) {
    data object Loading : DebtLogicListState()
    class Received(needToSetFilter: Boolean) :
        DebtLogicListState(needToSetFilter)
    data object Sorted : DebtLogicListState()
}