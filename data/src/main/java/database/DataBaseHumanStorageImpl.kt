package database

import android.content.Context
import androidx.room.Room
import com.breckneck.deptbook.data.storage.HumanStorage
import entity.Human

private const val HUMAN_ID = "zoneid"

class DataBaseHumanStorageImpl(context: Context) : HumanStorage {

    val db = Room.databaseBuilder(context, AppDataBase::class.java, "HumanDataBase").build()

    override fun getAllHumans(): List<Human> {
        val humanList = db.appDao().getAllHuman()
        return humanList
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
        db.appDao().insertHuman(human)
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