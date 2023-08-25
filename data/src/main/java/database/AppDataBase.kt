package database

import androidx.room.Database
import androidx.room.RoomDatabase
import entity.Debt
import entity.Human


@Database (entities = [Human::class, Debt::class], version = 5)
abstract class AppDataBase: RoomDatabase() {
    abstract fun appDao() : AppDao
}