package com.breckneck.deptbook.domain.usecase.Finance

import com.breckneck.deptbook.domain.model.Finance
import com.breckneck.deptbook.domain.repository.FinanceRepository

class GetFinanceByCategoryId(private val financeRepository: FinanceRepository) {

    fun execute(categoryId: Int): List<Finance> {
        return financeRepository.getFinanceByCategoryId(categoryId = categoryId)
    }
}