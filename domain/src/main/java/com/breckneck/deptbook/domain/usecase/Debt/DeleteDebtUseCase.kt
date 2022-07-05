package com.breckneck.deptbook.domain.usecase.Debt

import com.breckneck.deptbook.domain.model.DebtDomain
import com.breckneck.deptbook.domain.repository.DebtRepository

class DeleteDebtUseCase(val debtRepository: DebtRepository) {

    fun execute(debtDomain: DebtDomain) {
        debtRepository.deleteDebt(debtDomain)
    }
}