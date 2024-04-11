package database

import android.content.Context
import androidx.room.Room
import com.breckneck.deptbook.data.storage.FinanceStorage
import entity.FinanceData
import util.DATA_BASE_NAME

class DataBaseFinanceStorageImpl(context: Context): FinanceStorage {

    private val db = Room.databaseBuilder(context, AppDataBase::class.java, DATA_BASE_NAME)
        .build()

    override fun setFinance(finance: FinanceData) {
        db.appDao().insertFinance(financeData = finance)
    }

    override fun getAllFinance(): List<FinanceData> {
        return db.appDao().getAllFinances()
    }

    override fun getFinanceByCategoryId(
        categoryId: Int
    ): List<FinanceData> {
        return db.appDao().getFinanceByCategoryId(categoryId = categoryId)
    }

    override fun deleteFinance(financeData: FinanceData) {
        db.appDao().deleteFinance(financeData = financeData)
    }

    override fun updateFinance(financeData: FinanceData) {
        db.appDao().updateFinance(financeData = financeData)
    }
}