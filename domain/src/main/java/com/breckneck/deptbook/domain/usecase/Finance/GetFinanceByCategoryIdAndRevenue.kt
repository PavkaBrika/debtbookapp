package com.breckneck.deptbook.domain.usecase.Finance

import com.breckneck.deptbook.domain.model.Finance
import com.breckneck.deptbook.domain.repository.FinanceRepository

class GetFinanceByCategoryIdAndRevenue(private val financeRepository: FinanceRepository) {

    fun execute(categoryId: Int, isRevenue: Boolean): List<Finance> {
        return financeRepository.getFinanceByCategoryIdAndRevenue(categoryId = categoryId, isRevenue = isRevenue)
    }
}