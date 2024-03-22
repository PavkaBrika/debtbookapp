package database

import androidx.room.Database
import androidx.room.RoomDatabase
import entity.Debt
import entity.FinanceCategoryData
import entity.FinanceData
import entity.Human


@Database (entities = [Human::class, Debt::class, FinanceData::class, FinanceCategoryData::class], version = 11)
//@Database (entities = [Human::class, Debt::class, FinanceData::class], version = 6)
abstract class AppDataBase: RoomDatabase() {
    abstract fun appDao() : AppDao
}