package com.breckneck.deptbook.domain.usecase

import com.breckneck.deptbook.domain.repository.DebtRepository

class GetAllDebtsUseCase(val debtRepository: DebtRepository) {

    fun execute(id: Int) {
        debtRepository.getAllDebtsById(id = id)
    }
}