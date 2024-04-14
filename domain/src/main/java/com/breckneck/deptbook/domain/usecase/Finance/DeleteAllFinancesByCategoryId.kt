package com.breckneck.deptbook.domain.usecase.Finance

import com.breckneck.deptbook.domain.repository.FinanceRepository

class DeleteAllFinancesByCategoryId(private val financeRepository: FinanceRepository) {

    fun execute(financeCategoryId: Int) {
        financeRepository.deleteFinanceByCategoryId(financeCategoryId = financeCategoryId)
    }
}