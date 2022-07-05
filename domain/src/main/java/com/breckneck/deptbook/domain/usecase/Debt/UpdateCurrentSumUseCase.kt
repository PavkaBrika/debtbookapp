package com.breckneck.deptbook.domain.usecase.Debt

class UpdateCurrentSumUseCase {
    fun execute(sum: Double, pastSum: Double) : Double {
        return sum - pastSum
    }
}