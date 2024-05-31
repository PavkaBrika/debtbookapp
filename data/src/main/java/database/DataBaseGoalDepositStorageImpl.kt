package database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.breckneck.deptbook.data.storage.GoalDepositStorage
import entity.GoalDepositData
import util.DATA_BASE_NAME

class DataBaseGoalDepositStorageImpl(private val context: Context): GoalDepositStorage {

    val db = Room.databaseBuilder(context, AppDataBase::class.java, DATA_BASE_NAME).build()

    override fun setGoalDepositData(goalDepositData: GoalDepositData) {
        db.appDao().insertGoalDeposit(goalDepositData = goalDepositData)
    }

    override fun getGoalDepositsDataByGoalId(goalId: Int): List<GoalDepositData> {
        return db.appDao().getGoalDepositsByGoalId(goalId = goalId)
    }

    override fun deleteGoalDepositsDataByGoalId(goalId: Int) {
        db.appDao().deleteGoalDepositsByGoalId(goalId = goalId)
    }
}