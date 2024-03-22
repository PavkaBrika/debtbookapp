package database

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.breckneck.deptbook.data.storage.FinanceStorage
import entity.FinanceCategoryData
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


    override fun getAllFinanceCategories(): List<FinanceCategoryData> {
        return db.appDao().getAllFinanceCategories()
    }
}