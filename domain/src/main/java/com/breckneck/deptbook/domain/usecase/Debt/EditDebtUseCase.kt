package com.breckneck.deptbook.domain.usecase.Debt

import com.breckneck.deptbook.domain.model.DebtDomain
import com.breckneck.deptbook.domain.repository.DebtRepository

class EditDebtUseCase(val debtRepository: DebtRepository) {

    fun execute(id: Int, sum: Double, idHuman: Int, info: String?, date: String) {
        debtRepository.editDebt(DebtDomain(id, sum, idHuman, info, date))
    }
}