package database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import entity.Debt
import entity.FinanceCategoryData
import entity.FinanceData
import entity.GoalData
import entity.GoalDepositData
import entity.Human


@Database (entities = [Human::class, Debt::class, FinanceData::class, FinanceCategoryData::class, GoalData::class, GoalDepositData::class], version = 14)
@TypeConverters(Converters::class)
abstract class AppDataBase: RoomDatabase() {
    abstract fun appDao() : AppDao
}