package database

import android.content.Context
import androidx.room.Room
import com.breckneck.deptbook.data.storage.FinanceCategoryStorage
import entity.FinanceCategoryData
import entity.relations.FinanceCategoryDataWithFinanceData
import util.DATA_BASE_NAME
import util.FinanceCategoryStateData

class DataBaseFinanceCategoryStorageImpl(context: Context) : FinanceCategoryStorage {

    private val db = AppDataBase(context = context)

    override fun getAllFinanceCategories(): List<FinanceCategoryData> {
        return db.appDao().getAllFinanceCategories()
    }

    override fun getAllCategoriesWithFinances(): List<FinanceCategoryDataWithFinanceData> {
        return db.appDao().getAllFinanceCategoriesWithFinances()
    }

    override fun setFinanceCategory(category: FinanceCategoryData) {
        db.appDao().insertFinanceCategory(financeCategoryData = category)
    }

    override fun deleteFinanceCategory(category: FinanceCategoryData) {
        db.appDao().deleteFinanceCategory(financeCategoryData = category)
    }

    override fun getFinanceCategoriesByState(financeCategoryStateData: FinanceCategoryStateData): List<FinanceCategoryData> {
        return db.appDao().getFinanceCategoriesByState(financeCategoryStateData = financeCategoryStateData)
    }

    override fun replaceAllFinanceCategories(financeCategoryDataList: List<FinanceCategoryData>) {
        db.appDao().deleteAllFinanceCategories()
        db.appDao().insertAllFinanceCategories(financeCategoryDataList = financeCategoryDataList)
    }
}