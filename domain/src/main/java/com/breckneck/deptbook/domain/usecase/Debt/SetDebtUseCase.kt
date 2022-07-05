package com.breckneck.deptbook.domain.usecase.Debt

import com.breckneck.deptbook.domain.model.DebtDomain
import com.breckneck.deptbook.domain.repository.DebtRepository

class SetDebtUseCase(val debtRepository: DebtRepository) {

        fun execute(sum: Double, currency: String, idHuman: Int, info: String?, date: String) {
        debtRepository.setDebt(DebtDomain(id = 0 , sum = sum, currency = currency, idHuman = idHuman, info = info, date = date))
    }
}