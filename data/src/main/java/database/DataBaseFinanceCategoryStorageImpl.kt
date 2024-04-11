package database

import android.content.Context
import androidx.room.Room
import com.breckneck.deptbook.data.storage.FinanceCategoryStorage
import entity.FinanceCategoryData
import entity.relations.FinanceCategoryDataWithFinanceData
import util.DATA_BASE_NAME

class DataBaseFinanceCategoryStorageImpl(context: Context) : FinanceCategoryStorage {

    private val db = Room.databaseBuilder(context, AppDataBase::class.java, DATA_BASE_NAME).build()


    override fun getAllFinanceCategories(): List<FinanceCategoryData> {
        return db.appDao().getAllFinanceCategories()
    }

    override fun getAllCategoriesWithFinances(): List<FinanceCategoryDataWithFinanceData> {
        return db.appDao().getAllFinanceCategoriesWithFinances()
    }

    override fun setFinanceCategory(category: FinanceCategoryData) {
        return db.appDao().insertFinanceCategory(financeCategoryData = category)
    }

    override fun deleteFinanceCategory(category: FinanceCategoryData) {
        return db.appDao().deleteFinanceCategory(financeCategoryData = category)
    }
}