package database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import entity.Debt
import entity.FinanceCategoryData
import entity.FinanceData
import entity.Human


@Database (entities = [Human::class, Debt::class, FinanceData::class, FinanceCategoryData::class], version = 13)
@TypeConverters(Converters::class)
abstract class AppDataBase: RoomDatabase() {
    abstract fun appDao() : AppDao
}