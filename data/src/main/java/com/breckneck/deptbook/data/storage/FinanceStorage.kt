package com.breckneck.deptbook.data.storage

import entity.FinanceData

interface FinanceStorage {

    fun setFinance(finance: FinanceData)

    fun getAllFinance(): List<FinanceData>

    fun getFinanceByCategoryIdAndRevenue(categoryId: Int, isRevenue: Boolean): List<FinanceData>
}