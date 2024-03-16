package com.breckneck.deptbook.data.storage

import com.breckneck.deptbook.domain.model.Finance
import entity.FinanceData

interface FinanceStorage {

    fun setFinance(finance: FinanceData)

    fun getAllFinance(): List<FinanceData>

}