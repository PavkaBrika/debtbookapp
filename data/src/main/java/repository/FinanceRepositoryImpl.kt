package repository

import com.breckneck.deptbook.data.storage.FinanceStorage
import com.breckneck.deptbook.domain.model.Finance
import com.breckneck.deptbook.domain.model.FinanceCategory
import com.breckneck.deptbook.domain.repository.FinanceRepository
import entity.FinanceData

class FinanceRepositoryImpl(private val financeStorage: FinanceStorage) : FinanceRepository {

    override fun setFinance(finance: Finance) {
        financeStorage.setFinance(
            finance = FinanceData(
                id = finance.id,
                name = finance.name,
                sum = finance.sum,
                isRevenue = finance.isRevenue,
                info = finance.info,
                financeCategoryId = finance.financeCategoryId
            )
        )
    }

    override fun getAllFinance(): List<Finance> {
        return financeStorage.getAllFinance().map { financeData ->
            Finance(
                id = financeData.id,
                name = financeData.name,
                sum = financeData.sum,
                isRevenue = financeData.isRevenue,
                info = financeData.info,
                financeCategoryId = financeData.financeCategoryId
            )
        }
    }
}