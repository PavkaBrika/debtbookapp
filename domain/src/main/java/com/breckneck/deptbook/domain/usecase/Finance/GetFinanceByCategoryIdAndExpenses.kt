package com.breckneck.deptbook.domain.usecase.Finance

import com.breckneck.deptbook.domain.model.Finance
import com.breckneck.deptbook.domain.repository.FinanceRepository

class GetFinanceByCategoryIdAndExpenses(private val financeRepository: FinanceRepository) {

    fun execute(categoryId: Int, isExpenses: Boolean): List<Finance> {
        return financeRepository.getFinanceByCategoryIdAndExpenses(categoryId = categoryId, isExpenses = isExpenses)
    }
}