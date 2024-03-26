package repository

import com.breckneck.deptbook.data.storage.FinanceCategoryStorage
import com.breckneck.deptbook.domain.model.FinanceCategory
import com.breckneck.deptbook.domain.repository.FinanceCategoryRepository
import com.breckneck.deptbook.domain.repository.FinanceRepository
import entity.FinanceCategoryData

class FinanceCategoryRepositoryImpl(private val financeCategoryStorage: FinanceCategoryStorage) :
    FinanceCategoryRepository {

    override fun getAllFinanceCategories(): List<FinanceCategory> {
        return financeCategoryStorage.getAllFinanceCategories().map { financeCategoryData ->
            FinanceCategory(
                id = financeCategoryData.id,
                name = financeCategoryData.name,
                color = financeCategoryData.color,
                image = financeCategoryData.image
            )
        }
    }

    override fun setFinanceCategory(category: FinanceCategory) {
        financeCategoryStorage.setFinanceCategory(
            FinanceCategoryData(
                id = category.id,
                name = category.name,
                color = category.color,
                image = category.image
            )
        )
    }
}