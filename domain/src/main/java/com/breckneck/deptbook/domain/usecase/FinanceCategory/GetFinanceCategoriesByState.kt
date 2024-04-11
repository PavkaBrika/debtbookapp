package com.breckneck.deptbook.domain.usecase.FinanceCategory

import com.breckneck.deptbook.domain.model.FinanceCategory
import com.breckneck.deptbook.domain.repository.FinanceCategoryRepository
import com.breckneck.deptbook.domain.util.FinanceCategoryState

class GetFinanceCategoriesByState(private val financeCategoryRepository: FinanceCategoryRepository) {

    fun execute(financeCategoryState: FinanceCategoryState): List<FinanceCategory> {
        return financeCategoryRepository.getFinanceCategoriesByState(financeCategoryState = financeCategoryState)
    }
}