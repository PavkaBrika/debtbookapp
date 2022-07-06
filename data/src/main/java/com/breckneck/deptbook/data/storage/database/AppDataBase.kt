package com.breckneck.deptbook.data.storage.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.breckneck.deptbook.data.storage.entity.Debt
import com.breckneck.deptbook.data.storage.entity.Human


@Database (entities = [Human::class, Debt::class], version = 5)
abstract class AppDataBase: RoomDatabase() {
    abstract fun appDao() : AppDao
}