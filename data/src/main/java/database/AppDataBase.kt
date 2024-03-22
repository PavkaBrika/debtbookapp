package database

import androidx.room.Database
import androidx.room.RoomDatabase
import entity.Debt
import entity.FinanceCategoryData
import entity.FinanceData
import entity.Human


//@Database (entities = [Human::class, Debt::class, FinanceData::class, FinanceCategoryData::class], version = 6)
@Database (entities = [Human::class, Debt::class, FinanceData::class], version = 10)
abstract class AppDataBase: RoomDatabase() {
    abstract fun appDao() : AppDao
}