package com.breckneck.deptbook.data.storage.database

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.breckneck.deptbook.data.storage.HumanStorage
import com.breckneck.deptbook.data.storage.entity.Human

private val SHARED_PREFS_NAME = "shared_prefs_name"
private val ZONE_ID = "zoneid"

class DataBaseHumanStorageImpl(context: Context) : HumanStorage {



    val sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
    val db = Room.databaseBuilder(context, AppDataBase::class.java, "HumanDataBase").build()

    override fun getAllHumans(): List<Human> {
        val humanList = db.appDao().getAllHuman()
        return humanList
    }

    override fun insertHuman(human: Human) {
        var humanid = sharedPreferences.getInt(ZONE_ID, 0)
        human.id = humanid
        humanid++
        db.appDao().insertHuman(human)
        sharedPreferences.edit().putInt(ZONE_ID, humanid).apply()
    }

    override fun getLastHumanId(): Int {
        return db.appDao().getLastHumanId()
    }

    override fun addSum(humanId: Int, sum: Double) {
        db.appDao().addSum(humanId = humanId, sum = sum)
    }

}