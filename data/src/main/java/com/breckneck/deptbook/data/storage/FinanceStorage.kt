package com.breckneck.deptbook.data.storage

import entity.FinanceCategoryData
import entity.FinanceData

interface FinanceStorage {

    fun setFinance(finance: FinanceData)

    fun getAllFinance(): List<FinanceData>
}