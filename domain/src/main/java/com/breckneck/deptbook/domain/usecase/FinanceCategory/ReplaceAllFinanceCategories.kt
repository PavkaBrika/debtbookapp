package com.breckneck.deptbook.domain.usecase.FinanceCategory

import com.breckneck.deptbook.domain.model.FinanceCategory
import com.breckneck.deptbook.domain.repository.FinanceCategoryRepository

class ReplaceAllFinanceCategories(private val financeCategoryRepository: FinanceCategoryRepository) {

    fun execute(financeCategoryList: List<FinanceCategory>) {
        financeCategoryRepository.replaceAllFinanceCategories(financeCategoriesList = financeCategoryList)
    }
}