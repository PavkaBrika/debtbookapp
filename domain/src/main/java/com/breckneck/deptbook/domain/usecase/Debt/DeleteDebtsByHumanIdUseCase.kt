package com.breckneck.deptbook.domain.usecase.Debt

import com.breckneck.deptbook.domain.repository.DebtRepository

class DeleteDebtsByHumanIdUseCase(val debtRepository: DebtRepository) {

    fun execute(id: Int) {
        debtRepository.deleteDebtsByHumanId(id = id)
    }
}