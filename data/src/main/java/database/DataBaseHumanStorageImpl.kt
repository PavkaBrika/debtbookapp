package database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.breckneck.deptbook.data.storage.HumanStorage
import entity.Human
import util.DATA_BASE_NAME

class DataBaseHumanStorageImpl(context: Context) : HumanStorage {

    val MIGRATION_5_11 = object : Migration(5 ,11) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS 'FinanceData' ('id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'name' TEXT NOT NULL, 'sum' REAL NOT NULL, 'info' TEXT, 'financeCategoryId' INTEGER NOT NULL)")
            database.execSQL("CREATE TABLE IF NOT EXISTS 'FinanceCategoryData' ('id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'name' TEXT NOT NULL, 'color' TEXT NOT NULL, 'image' INTEGER NOT NULL)")
            database.execSQL("INSERT OR IGNORE INTO 'FinanceCategoryData' ('id', 'name', 'color', 'image') VALUES ('1', 'Health', 'EF9A9A', '1')")
            database.execSQL("INSERT OR IGNORE INTO 'FinanceCategoryData' ('id', 'name', 'color', 'image') VALUES ('2', 'Entertainment', 'F48FB1', '2')")
            database.execSQL("INSERT OR IGNORE INTO 'FinanceCategoryData' ('id', 'name', 'color', 'image') VALUES ('3', 'Home', 'CE93D8', '3')")
            database.execSQL("INSERT OR IGNORE INTO 'FinanceCategoryData' ('id', 'name', 'color', 'image') VALUES ('4', 'Education', 'B39DDB', '4')")
            database.execSQL("INSERT OR IGNORE INTO 'FinanceCategoryData' ('id', 'name', 'color', 'image') VALUES ('5', 'Presents', '9FA8DA', '5')")
            database.execSQL("INSERT OR IGNORE INTO 'FinanceCategoryData' ('id', 'name', 'color', 'image') VALUES ('6', 'Food', '80DEEA', '6')")
            database.execSQL("INSERT OR IGNORE INTO 'FinanceCategoryData' ('id', 'name', 'color', 'image') VALUES ('7', 'Other', '90CAF9', '7')")
        }
    }

    val MIGRATION_11_12 = object: Migration(11, 12) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS 'Debt_new'('id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'sum' REAL NOT NULL, 'idHuman' INTEGER NOT NULL, 'info' TEXT, 'date' TEXT NOT NULL)")
            database.execSQL("INSERT INTO 'Debt_new' SELECT * FROM 'Debt'")
            database.execSQL("DROP TABLE IF EXISTS 'Debt'")
            database.execSQL("ALTER TABLE 'Debt_new' RENAME TO 'Debt'")
        }
    }

    val MIGRATION_12_13 = object: Migration(12, 13) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS 'Human_new'('id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'name' TEXT NOT NULL, 'sumDebt' REAL NOT NULL, 'currency' TEXT NOT NULL)")
            database.execSQL("INSERT INTO 'Human_new' SELECT * FROM 'Human'")
            database.execSQL("DROP TABLE IF EXISTS 'Human'")
            database.execSQL("ALTER TABLE 'Human_new' RENAME TO 'Human'")
        }
    }

    val MIGRATION_13_14 = object: Migration(13, 14) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE 'FinanceData' ADD COLUMN 'isRevenue' INTEGER NOT NULL")
        }
    }

    val roomDatabaseCallback = object: RoomDatabase.Callback() {
        override fun onCreate(database: SupportSQLiteDatabase) {
            database.execSQL("INSERT OR IGNORE INTO 'FinanceCategoryData' ('id', 'name', 'color', 'image') VALUES ('1', 'Health', 'EF9A9A', '1')")
            database.execSQL("INSERT OR IGNORE INTO 'FinanceCategoryData' ('id', 'name', 'color', 'image') VALUES ('2', 'Entertainment', 'F48FB1', '2')")
            database.execSQL("INSERT OR IGNORE INTO 'FinanceCategoryData' ('id', 'name', 'color', 'image') VALUES ('3', 'Home', 'CE93D8', '3')")
            database.execSQL("INSERT OR IGNORE INTO 'FinanceCategoryData' ('id', 'name', 'color', 'image') VALUES ('4', 'Education', 'B39DDB', '4')")
            database.execSQL("INSERT OR IGNORE INTO 'FinanceCategoryData' ('id', 'name', 'color', 'image') VALUES ('5', 'Presents', '9FA8DA', '5')")
            database.execSQL("INSERT OR IGNORE INTO 'FinanceCategoryData' ('id', 'name', 'color', 'image') VALUES ('6', 'Food', '80DEEA', '6')")
            database.execSQL("INSERT OR IGNORE INTO 'FinanceCategoryData' ('id', 'name', 'color', 'image') VALUES ('7', 'Other', '90CAF9', '7')")
            super.onCreate(database)
        }
    }

    val db = Room.databaseBuilder(context, AppDataBase::class.java, DATA_BASE_NAME)
        .addCallback(roomDatabaseCallback)
        .addMigrations(MIGRATION_5_11, MIGRATION_11_12, MIGRATION_12_13, MIGRATION_13_14)
        .build()

    override fun getAllHumans(): List<Human> {
        val humanList = db.appDao().getAllHuman()
        return humanList
    }

    override fun replaceAllHumans(humanList: List<Human>) {
        db.appDao().deleteAllHumans()
        db.appDao().insertAllHumans(humanList = humanList)
    }

    override fun getPositiveHumans(): List<Human> {
        val humanList = db.appDao().getPositiveHumans()
        return humanList
    }

    override fun getNegativeHumans(): List<Human> {
        val humanList = db.appDao().getNegativeHumans()
        return humanList
    }

    override fun insertHuman(human: Human) {
        db.appDao().insertHuman(human)
    }

    override fun getLastHumanId(): Int {
        return db.appDao().getLastHumanId()
    }

    override fun addSum(humanId: Int, sum: Double) {
        db.appDao().addSum(humanId = humanId, sum = sum)
    }

    override fun getAllDebtsSum(currency: String): List<Double> {
        return db.appDao().getAllDebtsSum(currency)
    }

    override fun getHumanSumDebtUseCase(humanId: Int): Double {
        return db.appDao().getHumanSumDebt(humanId = humanId)
    }

    override fun deleteHumanById(id: Int) {
        db.appDao().deleteHumanById(id = id)
    }

    override fun updateHuman(human: Human) {
        db.appDao().updateHuman(human = human)
    }
}