package com.breckneck.deptbook.domain.usecase.Debt

import com.breckneck.deptbook.domain.model.DebtDomain
import com.breckneck.deptbook.domain.repository.DebtRepository

class GetAllDebtsUseCase(val debtRepository: DebtRepository) {

    fun execute(id: Int) : List<DebtDomain> {
        return debtRepository.getAllDebtsById(id = id)
    }
}