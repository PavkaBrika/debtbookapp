package com.breckneck.deptbook.data.storage

import entity.FinanceData

interface FinanceStorage {

    fun setFinance(finance: FinanceData)

    fun getAllFinance(): List<FinanceData>

    fun getFinanceByCategoryId(categoryId: Int): List<FinanceData>

    fun deleteFinance(financeData: FinanceData)

    fun updateFinance(financeData: FinanceData)

    fun deleteAllFinancesByCategoryId(financeCategoryId: Int)

    fun replaceAllFinances(financeDataList: List<FinanceData>)
}