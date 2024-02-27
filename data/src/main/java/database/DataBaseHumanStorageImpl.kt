package database

import android.content.Context
import androidx.room.Room
import com.breckneck.deptbook.data.storage.HumanStorage
import entity.Human
import util.DATA_BASE_NAME

private const val SHARED_PREFS_HUMAN = "shared_prefs_name"
private const val HUMAN_ID = "zoneid"

class DataBaseHumanStorageImpl(context: Context) : HumanStorage {

    val sharedPreferences = context.getSharedPreferences(SHARED_PREFS_HUMAN, Context.MODE_PRIVATE)
    val db = Room.databaseBuilder(context, AppDataBase::class.java, DATA_BASE_NAME).build()

    override fun getAllHumans(): List<Human> {
        val humanList = db.appDao().getAllHuman()
        return humanList
    }

    override fun replaceAllHumans(humanList: List<Human>) {
        db.appDao().deleteAllHumans()
        db.appDao().insertAllHumans(humanList = humanList)
    }

    override fun getPositiveHumans(): List<Human> {
        val humanList = db.appDao().getPositiveHumans()
        return humanList
    }

    override fun getNegativeHumans(): List<Human> {
        val humanList = db.appDao().getNegativeHumans()
        return humanList
    }

    override fun insertHuman(human: Human) {
        var humanid = sharedPreferences.getInt(HUMAN_ID, 0)
        human.id = humanid
        humanid++
        db.appDao().insertHuman(human)
        sharedPreferences.edit().putInt(HUMAN_ID, humanid).apply()
    }

    override fun getLastHumanId(): Int {
        return db.appDao().getLastHumanId()
    }

    override fun addSum(humanId: Int, sum: Double) {
        db.appDao().addSum(humanId = humanId, sum = sum)
    }

    override fun getAllDebtsSum(currency: String): List<Double> {
        return db.appDao().getAllDebtsSum(currency)
    }

    override fun getHumanSumDebtUseCase(humanId: Int): Double {
        return db.appDao().getHumanSumDebt(humanId = humanId)
    }

    override fun deleteHumanById(id: Int) {
        db.appDao().deleteHumanById(id = id)
    }

    override fun updateHuman(human: Human) {
        db.appDao().updateHuman(human = human)
    }
}