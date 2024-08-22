package database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.breckneck.deptbook.data.storage.GoalDepositStorage
import entity.GoalDepositData
import util.DATA_BASE_NAME

class DataBaseGoalDepositStorageImpl(context: Context): GoalDepositStorage {

    private val db = AppDataBase(context = context)

    override fun setGoalDepositData(goalDepositData: GoalDepositData) {
        db.appDao().insertGoalDeposit(goalDepositData = goalDepositData)
    }

    override fun getGoalDepositsDataByGoalId(goalId: Int): List<GoalDepositData> {
        return db.appDao().getGoalDepositsByGoalId(goalId = goalId)
    }

    override fun deleteGoalDepositsDataByGoalId(goalId: Int) {
        db.appDao().deleteGoalDepositsByGoalId(goalId = goalId)
    }

    override fun replaceAllGoalDepositData(goalDepositDataList: List<GoalDepositData>) {
        db.appDao().deleteAllGoalDeposits()
        db.appDao().insertAllGoalDeposits(goalDepositDataList = goalDepositDataList)
    }

    override fun getAllGoalDepositsData(): List<GoalDepositData> {
        return db.appDao().getAllGoalDeposits()
    }
}