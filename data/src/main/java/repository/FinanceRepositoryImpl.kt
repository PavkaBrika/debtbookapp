package repository

import com.breckneck.deptbook.data.storage.FinanceStorage
import com.breckneck.deptbook.domain.model.Finance
import com.breckneck.deptbook.domain.repository.FinanceRepository
import entity.FinanceData

class FinanceRepositoryImpl(private val financeStorage: FinanceStorage): FinanceRepository {

    override fun setFinance(finance: Finance) {
        financeStorage.setFinance(finance = FinanceData(finance.id, finance.name, finance.sum, finance.info))
    }

    override fun getAllFinance(): List<Finance> {
        return financeStorage.getAllFinance().map { financeData ->
            Finance(financeData.id, financeData.name, financeData.sum, financeData.info)
        }
    }
}