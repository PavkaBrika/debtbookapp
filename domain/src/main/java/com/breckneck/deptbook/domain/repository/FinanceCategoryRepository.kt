package com.breckneck.deptbook.domain.repository

import com.breckneck.deptbook.domain.model.FinanceCategory

interface FinanceCategoryRepository {

    fun getAllFinanceCategories(): List<FinanceCategory>

    fun setFinanceCategory(category: FinanceCategory)
}