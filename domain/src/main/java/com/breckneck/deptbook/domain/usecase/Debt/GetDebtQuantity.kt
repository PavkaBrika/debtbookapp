package com.breckneck.deptbook.domain.usecase.Debt

import com.breckneck.deptbook.domain.repository.DebtRepository

class GetDebtQuantity(private val debtRepository: DebtRepository) {

    fun execute(): Int {
        return debtRepository.getDebtQuantity()
    }
}