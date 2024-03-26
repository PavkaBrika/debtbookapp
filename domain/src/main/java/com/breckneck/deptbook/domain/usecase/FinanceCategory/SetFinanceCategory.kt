package com.breckneck.deptbook.domain.usecase.FinanceCategory

import com.breckneck.deptbook.domain.model.FinanceCategory
import com.breckneck.deptbook.domain.repository.FinanceCategoryRepository

class SetFinanceCategory(private val financeCategoryRepository: FinanceCategoryRepository) {

    fun execute(financeCategory: FinanceCategory) {
        financeCategoryRepository.setFinanceCategory(category = financeCategory)
    }
}