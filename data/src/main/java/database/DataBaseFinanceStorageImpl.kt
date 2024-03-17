package database

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.breckneck.deptbook.data.storage.FinanceStorage
import entity.FinanceData
import util.DATA_BASE_NAME

class DataBaseFinanceStorageImpl(context: Context): FinanceStorage {

    val MIGRATION_5_6 = object : Migration(5 ,6) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS 'FinanceData' ('id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'name' TEXT NOT NULL, 'sum' REAL NOT NULL, 'info' TEXT NOT NULL)")
        }
    }

    private val db = Room.databaseBuilder(context, AppDataBase::class.java, DATA_BASE_NAME)
        .addMigrations(MIGRATION_5_6)
        .build()

    override fun setFinance(finance: FinanceData) {
        db.appDao().insertFinance(financeData = finance)
    }

    override fun getAllFinance(): List<FinanceData> {
        return db.appDao().getAllFinances()
    }
}