package com.breckneck.deptbook.domain.usecase.FinanceCategory

import com.breckneck.deptbook.domain.model.FinanceCategoryWithFinances
import com.breckneck.deptbook.domain.repository.FinanceCategoryRepository

class GetAllCategoriesWithFinances(private val financeCategoryRepository: FinanceCategoryRepository) {

    fun execute(): MutableList<FinanceCategoryWithFinances> {
        return financeCategoryRepository.getAllCategoriesWithFinances().toMutableList()
    }
}