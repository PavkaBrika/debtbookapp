package database

import android.content.Context
import androidx.room.Room
import com.breckneck.deptbook.data.storage.GoalStorage
import entity.GoalData
import util.DATA_BASE_NAME

class DataBaseGoalStorageImpl(private val context: Context): GoalStorage {

    val db = Room.databaseBuilder(context, AppDataBase::class.java, DATA_BASE_NAME).build()

    override fun getAllGoals(): List<GoalData> {
        return db.appDao().getAllGoals()
    }

    override fun setGoal(goal: GoalData) {
        db.appDao().insertGoal(goalDate = goal)
    }
}