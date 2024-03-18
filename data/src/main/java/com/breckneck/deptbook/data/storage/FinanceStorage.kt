package com.breckneck.deptbook.data.storage

import com.breckneck.deptbook.domain.model.Finance
import com.breckneck.deptbook.domain.model.FinanceCategory
import entity.FinanceCategoryData
import entity.FinanceData

interface FinanceStorage {

    fun setFinance(finance: FinanceData)

    fun getAllFinance(): List<FinanceData>


    fun getAllFinanceCategories(): List<FinanceCategoryData>

}