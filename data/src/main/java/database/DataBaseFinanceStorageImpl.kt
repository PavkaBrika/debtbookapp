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

    val MIGRATION_5_6 = object : Migration(5 ,6) {
        override fun migrate(database: SupportSQLiteDatabase) {
//            database.execSQL("CREATE TABLE IF NOT EXISTS 'FinanceData' ('id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'name' TEXT NOT NULL, 'sum' REAL NOT NULL, 'info' TEXT NOT NULL, FOREIGN KEY('financeCategoryId') REFERENCES 'FinanceCategoryData'('id') ON UPDATE NO ACTION ON DELETE CASCADE)")
            database.execSQL("CREATE TABLE IF NOT EXISTS 'FinanceData' ('id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'name' TEXT NOT NULL, 'sum' REAL NOT NULL, 'info' TEXT NOT NULL, 'financeCategoryId' INTEGER NOT NULL)")
            database.execSQL("CREATE TABLE IF NOT EXISTS 'FinanceCategoryData' ('id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'name' TEXT NOT NULL, 'color' TEXT NOT NULL, 'image' INTEGER NOT NULL)")
            database.execSQL("INSERT INTO 'FinanceCategoryData' ('name', 'color', 'image') VALUES ('Health', 'EF9A9A', '1')")
            database.execSQL("INSERT INTO 'FinanceCategoryData' ('name', 'color', 'image') VALUES ('Entertainment', 'F48FB1', '2')")
            database.execSQL("INSERT INTO 'FinanceCategoryData' ('name', 'color', 'image') VALUES ('Home', 'CE93D8', '2')")
            database.execSQL("INSERT INTO 'FinanceCategoryData' ('name', 'color', 'image') VALUES ('Education', 'B39DDB', '2')")
            database.execSQL("INSERT INTO 'FinanceCategoryData' ('name', 'color', 'image') VALUES ('Presents', '9FA8DA', '2')")
            database.execSQL("INSERT INTO 'FinanceCategoryData' ('name', 'color', 'image') VALUES ('Food', '80DEEA', '2')")
            database.execSQL("INSERT INTO 'FinanceCategoryData' ('name', 'color', 'image') VALUES ('Other', '90CAF9', '2')")
        }
    }

//    val MIGRATION_6_7 = object : Migration(6, 7) {
//        override fun migrate(database: SupportSQLiteDatabase) {
//
//        }
//    }
//
//    val MIGRATION_7_8 = object: Migration(7, 8) {
//        override fun migrate(database: SupportSQLiteDatabase) {
//            database.execSQL("ALTER TABLE 'FinanceData' ADD COLUMN 'financeCategoryId' INTEGER DEFAULT NULL")
//            database.execSQL("ALTER TABLE 'FinanceCategoryData' ADD COLUMN 'image' INTEGER DEFAULT NULL")
//        }
//    }

    private val db = Room.databaseBuilder(context, AppDataBase::class.java, DATA_BASE_NAME)
        .addMigrations(MIGRATION_5_6)
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