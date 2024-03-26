package com.breckneck.deptbook.data.storage

import entity.FinanceCategoryData

interface FinanceCategoryStorage {

    fun getAllFinanceCategories(): List<FinanceCategoryData>

    fun setFinanceCategory(category: FinanceCategoryData)

}