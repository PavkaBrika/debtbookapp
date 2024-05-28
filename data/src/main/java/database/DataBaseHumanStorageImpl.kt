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

    private val MIGRATION_5_11 = object : Migration(5 ,11) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS 'FinanceCategoryData' ('id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'name' TEXT NOT NULL, 'state' INT NOT NULL, 'color' TEXT NOT NULL, 'image' INTEGER NOT NULL)")
            database.execSQL("CREATE TABLE IF NOT EXISTS 'FinanceData' ('id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'sum' REAL NOT NULL, 'date' INTEGER NOT NULL, 'info' TEXT, 'financeCategoryId' INTEGER NOT NULL, FOREIGN KEY('financeCategoryId') REFERENCES 'FinanceCategoryData'('id') ON UPDATE NO ACTION ON DELETE CASCADE)")
            insertInitialFinanceCategoryData(database = database)
        }
    }

    private val MIGRATION_11_12 = object: Migration(11, 12) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS 'Debt_new'('id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'sum' REAL NOT NULL, 'idHuman' INTEGER NOT NULL, 'info' TEXT, 'date' TEXT NOT NULL)")
            database.execSQL("INSERT INTO 'Debt_new' SELECT * FROM 'Debt'")
            database.execSQL("DROP TABLE IF EXISTS 'Debt'")
            database.execSQL("ALTER TABLE 'Debt_new' RENAME TO 'Debt'")
        }
    }

    private val MIGRATION_12_13 = object: Migration(12, 13) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS 'Human_new'('id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'name' TEXT NOT NULL, 'sumDebt' REAL NOT NULL, 'currency' TEXT NOT NULL)")
            database.execSQL("INSERT INTO 'Human_new' SELECT * FROM 'Human'")
            database.execSQL("DROP TABLE IF EXISTS 'Human'")
            database.execSQL("ALTER TABLE 'Human_new' RENAME TO 'Human'")
        }
    }

    private val MIGRATION_13_14 = object: Migration(13, 14) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS 'GoalData' ('id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'name' TEXT NOT NULL, 'sum' REAL NOT NULL, 'savedSum' REAL NOT NULL, 'currency' TEXT NOT NULL, 'photoPath' TEXT, 'creationDate' INTEGER NOT NULL, 'goalDate' INTEGER)")
            database.execSQL("CREATE TABLE IF NOT EXISTS 'GoalDepositData' ('id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'sum' REAL NOT NULL, 'date' INTEGER NOT NULL, 'goalId' INTEGER NOT NULL, FOREIGN KEY('goalId') REFERENCES 'GoalData'('id') ON UPDATE NO ACTION ON DELETE CASCADE)")
        }
    }

    private val roomDatabaseCallback = object: RoomDatabase.Callback() {
        override fun onCreate(database: SupportSQLiteDatabase) {
            insertInitialFinanceCategoryData(database = database)
//            insertInitialScreenshotsData(database = database)
//            insertTestData(database = database)
            super.onCreate(database)
        }
    }

    /**
     *USE EMOJIS UNICODE  FOR IMAGES, 'U+' REPLACED WITH '0x'

     * USEFUL PAGES:

     * https://emojidb.org/education-emojis

     * https://emojis.wiki/ru/kontroller-dlya-videoigr/
     */
    fun insertInitialFinanceCategoryData(database: SupportSQLiteDatabase) {
        database.execSQL("INSERT OR IGNORE INTO 'FinanceCategoryData' ('id', 'name', 'state', 'color', 'image') VALUES ('1', 'Health', '1', '#EF9A9A', '0x2764')")
        database.execSQL("INSERT OR IGNORE INTO 'FinanceCategoryData' ('id', 'name', 'state', 'color', 'image') VALUES ('2', 'Entertainment', '1', '#CE93D8', '0x1F3AC')")
        database.execSQL("INSERT OR IGNORE INTO 'FinanceCategoryData' ('id', 'name', 'state', 'color', 'image') VALUES ('3', 'Home', '1', '#9FA8DA', '0x1F3E0')")
        database.execSQL("INSERT OR IGNORE INTO 'FinanceCategoryData' ('id', 'name', 'state', 'color', 'image') VALUES ('4', 'Education', '1', '#81D4FA', '0x1F393')")
        database.execSQL("INSERT OR IGNORE INTO 'FinanceCategoryData' ('id', 'name', 'state', 'color', 'image') VALUES ('5', 'Gifts', '1', '#80CBC4', '0x1F381')")
        database.execSQL("INSERT OR IGNORE INTO 'FinanceCategoryData' ('id', 'name', 'state', 'color', 'image') VALUES ('6', 'Food', '1', '#C5E1A5', '0x1F37D')")
        database.execSQL("INSERT OR IGNORE INTO 'FinanceCategoryData' ('id', 'name', 'state', 'color', 'image') VALUES ('7', 'Other', '1', '#FFF59D', '0x1F36D')")

        database.execSQL("INSERT OR IGNORE INTO 'FinanceCategoryData' ('id', 'name', 'state', 'color', 'image') VALUES ('8', 'Salary', '2', '#EF9A9A', '0x1F4B5')")
        database.execSQL("INSERT OR IGNORE INTO 'FinanceCategoryData' ('id', 'name', 'state', 'color', 'image') VALUES ('9', 'Gifts', '2', '#CE93D8', '0x1F381')")
        database.execSQL("INSERT OR IGNORE INTO 'FinanceCategoryData' ('id', 'name', 'state', 'color', 'image') VALUES ('10', 'Investments', '2', '#CE93D8', '0x1F4BC')")
        database.execSQL("INSERT OR IGNORE INTO 'FinanceCategoryData' ('id', 'name', 'state', 'color', 'image') VALUES ('11', 'Other', '2', '#FFF59D', '0x1F4CB')")
    }

    fun insertInitialScreenshotsData(database: SupportSQLiteDatabase) {
        //RUSSIAN
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('1', 'Алексей', '500', 'RUB')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('2', 'Игорь', '10000', 'RUB')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('3', 'София', '-3200', 'RUB')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('4', 'Мама', '-5000', 'RUB')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('5', 'Джон', '-350', 'USD')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('6', 'Серега', '200', 'RUB')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('7', 'Максим', '17000', 'RUB')")

        database.execSQL("INSERT OR IGNORE INTO 'Debt' ('id', 'sum', 'idHuman', 'info', 'date') VALUES ('1', '25000', '7', '', '5 февр. 2024 г.')")
        database.execSQL("INSERT OR IGNORE INTO 'Debt' ('id', 'sum', 'idHuman', 'info', 'date') VALUES ('2', '-1000', '7', '', '1 февр. 2024 г.')")
        database.execSQL("INSERT OR IGNORE INTO 'Debt' ('id', 'sum', 'idHuman', 'info', 'date') VALUES ('3', '-5000', '7', '', '18 янв. 2024 г.')")
        database.execSQL("INSERT OR IGNORE INTO 'Debt' ('id', 'sum', 'idHuman', 'info', 'date') VALUES ('4', '-3000', '7', 'на вечеринку', '11 янв. 2024 г.')")
        database.execSQL("INSERT OR IGNORE INTO 'Debt' ('id', 'sum', 'idHuman', 'info', 'date') VALUES ('5', '1000', '7', '', '3 янв. 2024 г.')")

        //ENGLISH
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('1', 'Sophia', '950', 'USD')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('2', 'Mother', '-565', 'USD')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('3', 'Zack', '-200', 'USD')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('4', 'Emma', '-350', 'EUR')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('5', 'Michael', '970', 'USD')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('6', 'Jacob', '400', 'USD')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('7', 'John', '50', 'USD')")

        database.execSQL("INSERT OR IGNORE INTO 'Debt' ('id', 'sum', 'idHuman', 'info', 'date') VALUES ('1', '-100', '5', '', '5 Feb 2024 y.')")
        database.execSQL("INSERT OR IGNORE INTO 'Debt' ('id', 'sum', 'idHuman', 'info', 'date') VALUES ('2', '200', '5', '', '1 Feb 2024 y.')")
        database.execSQL("INSERT OR IGNORE INTO 'Debt' ('id', 'sum', 'idHuman', 'info', 'date') VALUES ('3', '-50', '5', 'Finally took at least 50 dollars', '30 Jan 2024 y.')")
        database.execSQL("INSERT OR IGNORE INTO 'Debt' ('id', 'sum', 'idHuman', 'info', 'date') VALUES ('4', '420', '5', '', '22 Jan 2024 y.')")
        database.execSQL("INSERT OR IGNORE INTO 'Debt' ('id', 'sum', 'idHuman', 'info', 'date') VALUES ('5', '300', '5', '', '13 Jan 2024 y.')")
        database.execSQL("INSERT OR IGNORE INTO 'Debt' ('id', 'sum', 'idHuman', 'info', 'date') VALUES ('6', '200', '5', '', '2 Jan 2024 y.')")

        //FRENCH
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('1', 'Charlotte', '950', 'EUR')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('2', 'Charles', '-565', 'EUR')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('3', 'Alexandre', '-200', 'EUR')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('4', 'Colette', '-350', 'USD')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('5', 'Antoine', '970', 'EUR')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('6', 'Louis', '400', 'EUR')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('7', 'Gabriel', '50', 'EUR')")

        database.execSQL("INSERT OR IGNORE INTO 'Debt' ('id', 'sum', 'idHuman', 'info', 'date') VALUES ('1', '-100', '5', '', '5 févr. 2024 a')")
        database.execSQL("INSERT OR IGNORE INTO 'Debt' ('id', 'sum', 'idHuman', 'info', 'date') VALUES ('2', '200', '5', '', '1 févr. 2024 a')")
        database.execSQL("INSERT OR IGNORE INTO 'Debt' ('id', 'sum', 'idHuman', 'info', 'date') VALUES ('3', '-50', '5', 'Finalement, il a fallu au moins 50 euros', '30 janv. 2024 a')")
        database.execSQL("INSERT OR IGNORE INTO 'Debt' ('id', 'sum', 'idHuman', 'info', 'date') VALUES ('4', '420', '5', '', '22 janv. 2024 a')")
        database.execSQL("INSERT OR IGNORE INTO 'Debt' ('id', 'sum', 'idHuman', 'info', 'date') VALUES ('5', '300', '5', '', '13 janv. 2024 a')")
        database.execSQL("INSERT OR IGNORE INTO 'Debt' ('id', 'sum', 'idHuman', 'info', 'date') VALUES ('6', '200', '5', '', '2 janv. 2024 a')")

        //DEUTSCH
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('1', 'Bertha', '950', 'EUR')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('2', 'Noah', '-565', 'EUR')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('3', 'Leo', '-200', 'EUR')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('4', 'Emma', '-350', 'USD')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('5', 'Paul', '970', 'EUR')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('6', 'Matteo', '400', 'EUR')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('7', 'Elijah', '50', 'EUR')")

        database.execSQL("INSERT OR IGNORE INTO 'Debt' ('id', 'sum', 'idHuman', 'info', 'date') VALUES ('1', '-100', '5', '', '5 Feb 2024 j.')")
        database.execSQL("INSERT OR IGNORE INTO 'Debt' ('id', 'sum', 'idHuman', 'info', 'date') VALUES ('2', '200', '5', '', '1 Feb 2024 j.')")
        database.execSQL("INSERT OR IGNORE INTO 'Debt' ('id', 'sum', 'idHuman', 'info', 'date') VALUES ('3', '-50', '5', 'Hat schließlich mindestens 50 Euro gekostet', '30 Jan 2024 j.')")
        database.execSQL("INSERT OR IGNORE INTO 'Debt' ('id', 'sum', 'idHuman', 'info', 'date') VALUES ('4', '420', '5', '', '22 Jan 2024 j.')")
        database.execSQL("INSERT OR IGNORE INTO 'Debt' ('id', 'sum', 'idHuman', 'info', 'date') VALUES ('5', '300', '5', '', '13 Jan 2024 j.')")
        database.execSQL("INSERT OR IGNORE INTO 'Debt' ('id', 'sum', 'idHuman', 'info', 'date') VALUES ('6', '200', '5', '', '2 Jan 2024 j.')")
    }

    private fun insertTestData(database: SupportSQLiteDatabase) {
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('1', 'Алексей', '500000', 'RUB')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('2', 'Игорь', '10000', 'RUB')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('3', 'София', '-3200', 'RUB')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('4', 'Мама', '-5000', 'RUB')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('5', 'Джон', '-350', 'USD')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('6', 'Серега', '200', 'RUB')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('7', 'Максим', '17000', 'RUB')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('8', 'Алексей', '500', 'RUB')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('9', 'Игорь', '10000', 'RUB')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('10', 'София', '-3200', 'RUB')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('11', 'Мама', '-5000', 'RUB')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('12', 'Джон', '-350', 'USD')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('13', 'Серега', '200', 'RUB')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('14', 'Максим', '17000', 'RUB')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('15', 'Алексей', '500', 'RUB')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('16', 'Игорь', '10000', 'RUB')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('17', 'София', '-3200', 'RUB')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('18', 'Мама', '-5000', 'RUB')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('19', 'Джон', '-350', 'USD')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('20', 'Серега', '200', 'RUB')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('21', 'Максим', '17000', 'RUB')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('22', 'Алексей', '500', 'RUB')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('23', 'Игорь', '10000', 'RUB')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('24', 'София', '-3200', 'RUB')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('25', 'Мама', '-5000', 'RUB')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('26', 'Джон', '-350', 'USD')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('27', 'Серега', '200', 'RUB')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('28', 'Максим', '17000', 'RUB')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('29', 'Алексей', '500', 'RUB')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('30', 'Игорь', '10000', 'RUB')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('31', 'София', '-3200', 'RUB')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('32', 'Мама', '-5000', 'RUB')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('51', 'Джон', '-350', 'USD')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('6', 'Серега', '200', 'RUB')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('7', 'Максим', '17000', 'RUB')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('8', 'Алексей', '500', 'RUB')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('9', 'Игорь', '10000', 'RUB')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('10', 'София', '-3200', 'RUB')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('11', 'Мама', '-5000', 'RUB')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('12', 'Джон', '-350', 'USD')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('13', 'Серега', '200', 'RUB')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('14', 'Максим', '17000', 'RUB')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('15', 'Алексей', '500', 'RUB')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('16', 'Игорь', '10000', 'RUB')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('17', 'София', '-3200', 'RUB')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('18', 'Мама', '-5000', 'RUB')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('19', 'Джон', '-350', 'USD')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('20', 'Серега', '200', 'RUB')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('21', 'Максим', '17000', 'RUB')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('22', 'Алексей', '500', 'RUB')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('23', 'Игорь', '10000', 'RUB')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('24', 'София', '-3200', 'RUB')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('25', 'Мама', '-5000', 'RUB')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('26', 'Джон', '-350', 'USD')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('27', 'Серега', '200', 'RUB')")
        database.execSQL("INSERT OR IGNORE INTO 'Human' ('id', 'name', 'sumDebt', 'currency') VALUES ('28', 'Максим', '17000', 'RUB')")

        database.execSQL("INSERT OR IGNORE INTO 'Debt' ('id', 'sum', 'idHuman', 'info', 'date') VALUES ('1', '25000', '1', '', '5 февр. 2024 г.')")
        database.execSQL("INSERT OR IGNORE INTO 'Debt' ('id', 'sum', 'idHuman', 'info', 'date') VALUES ('2', '-1000', '1', '', '1 февр. 2024 г.')")
        database.execSQL("INSERT OR IGNORE INTO 'Debt' ('id', 'sum', 'idHuman', 'info', 'date') VALUES ('3', '-5000', '1', '', '18 янв. 2024 г.')")
        database.execSQL("INSERT OR IGNORE INTO 'Debt' ('id', 'sum', 'idHuman', 'info', 'date') VALUES ('4', '-3000', '1', 'на вечеринку', '11 янв. 2024 г.')")
        database.execSQL("INSERT OR IGNORE INTO 'Debt' ('id', 'sum', 'idHuman', 'info', 'date') VALUES ('5', '1000', '1', '', '3 янв. 2024 г.')")
        database.execSQL("INSERT OR IGNORE INTO 'Debt' ('id', 'sum', 'idHuman', 'info', 'date') VALUES ('11', '25000', '1', '', '5 февр. 2024 г.')")
        database.execSQL("INSERT OR IGNORE INTO 'Debt' ('id', 'sum', 'idHuman', 'info', 'date') VALUES ('21', '-1000', '1', '', '1 февр. 2024 г.')")
        database.execSQL("INSERT OR IGNORE INTO 'Debt' ('id', 'sum', 'idHuman', 'info', 'date') VALUES ('31', '-5000', '1', '', '18 янв. 2024 г.')")
        database.execSQL("INSERT OR IGNORE INTO 'Debt' ('id', 'sum', 'idHuman', 'info', 'date') VALUES ('41', '-3000', '1', 'на вечеринку', '11 янв. 2024 г.')")
        database.execSQL("INSERT OR IGNORE INTO 'Debt' ('id', 'sum', 'idHuman', 'info', 'date') VALUES ('51', '1000', '1', '', '3 янв. 2024 г.')")
        database.execSQL("INSERT OR IGNORE INTO 'Debt' ('id', 'sum', 'idHuman', 'info', 'date') VALUES ('12', '25000', '1', '', '5 февр. 2024 г.')")
        database.execSQL("INSERT OR IGNORE INTO 'Debt' ('id', 'sum', 'idHuman', 'info', 'date') VALUES ('22', '-1000', '1', '', '1 февр. 2024 г.')")
        database.execSQL("INSERT OR IGNORE INTO 'Debt' ('id', 'sum', 'idHuman', 'info', 'date') VALUES ('32', '-5000', '1', '', '18 янв. 2024 г.')")
        database.execSQL("INSERT OR IGNORE INTO 'Debt' ('id', 'sum', 'idHuman', 'info', 'date') VALUES ('42', '-3000', '1', 'на вечеринку', '11 янв. 2024 г.')")
        database.execSQL("INSERT OR IGNORE INTO 'Debt' ('id', 'sum', 'idHuman', 'info', 'date') VALUES ('52', '1000', '1', '', '3 янв. 2024 г.')")
        database.execSQL("INSERT OR IGNORE INTO 'Debt' ('id', 'sum', 'idHuman', 'info', 'date') VALUES ('14', '25000', '1', '', '5 февр. 2024 г.')")
        database.execSQL("INSERT OR IGNORE INTO 'Debt' ('id', 'sum', 'idHuman', 'info', 'date') VALUES ('24', '-1000', '1', '', '1 февр. 2024 г.')")
        database.execSQL("INSERT OR IGNORE INTO 'Debt' ('id', 'sum', 'idHuman', 'info', 'date') VALUES ('34', '-5000', '1', '', '18 янв. 2024 г.')")
        database.execSQL("INSERT OR IGNORE INTO 'Debt' ('id', 'sum', 'idHuman', 'info', 'date') VALUES ('44', '-3000', '1', 'на вечеринку', '11 янв. 2024 г.')")
        database.execSQL("INSERT OR IGNORE INTO 'Debt' ('id', 'sum', 'idHuman', 'info', 'date') VALUES ('54', '1000', '1', '', '3 янв. 2024 г.')")
        database.execSQL("INSERT OR IGNORE INTO 'Debt' ('id', 'sum', 'idHuman', 'info', 'date') VALUES ('14', '25000', '1', '', '5 февр. 2024 г.')")
        database.execSQL("INSERT OR IGNORE INTO 'Debt' ('id', 'sum', 'idHuman', 'info', 'date') VALUES ('24', '-1000', '1', '', '1 февр. 2024 г.')")
        database.execSQL("INSERT OR IGNORE INTO 'Debt' ('id', 'sum', 'idHuman', 'info', 'date') VALUES ('34', '-5000', '1', '', '18 янв. 2024 г.')")
        database.execSQL("INSERT OR IGNORE INTO 'Debt' ('id', 'sum', 'idHuman', 'info', 'date') VALUES ('44', '-3000', '1', 'на вечеринку', '11 янв. 2024 г.')")
        database.execSQL("INSERT OR IGNORE INTO 'Debt' ('id', 'sum', 'idHuman', 'info', 'date') VALUES ('54', '1000', '1', '', '3 янв. 2024 г.')")
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