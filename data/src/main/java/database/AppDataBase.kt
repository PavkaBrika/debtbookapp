package database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import entity.Debt
import entity.FinanceCategoryData
import entity.FinanceData
import entity.GoalData
import entity.GoalDepositData
import entity.Human
import util.DATA_BASE_NAME


@Database(
    entities = [Human::class, Debt::class, FinanceData::class, FinanceCategoryData::class, GoalData::class, GoalDepositData::class],
    version = 14
)
@TypeConverters(Converters::class)
abstract class AppDataBase : RoomDatabase() {

    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var instance: AppDataBase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also { instance = it }
        }

        operator fun invoke(
            context: Context,
            callback: Callback,
            vararg migrations: Migration
        ) = instance ?: synchronized(LOCK) {
            instance ?: createDatabaseWithMigration(
                context,
                callback,
                *migrations
            ).also { instance = it }
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(context, AppDataBase::class.java, DATA_BASE_NAME).build()

        private fun createDatabaseWithMigration(
            context: Context,
            roomDatabaseCallback: Callback,
            vararg migrations: Migration
        ) =
            Room.databaseBuilder(context, AppDataBase::class.java, DATA_BASE_NAME)
                .addCallback(roomDatabaseCallback)
                .addMigrations(*migrations)
                .build()
    }
}