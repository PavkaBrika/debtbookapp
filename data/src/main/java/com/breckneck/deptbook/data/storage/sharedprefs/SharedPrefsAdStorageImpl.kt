package com.breckneck.deptbook.data.storage.sharedprefs

import android.content.Context
import com.breckneck.deptbook.data.storage.AdStorage

private val SHARED_PREFS_NAME_3 = "shared_prefs_name_3"
private val AD_ID = "adid"

class SharedPrefsAdStorageImpl(context: Context): AdStorage {

    val sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME_3, Context.MODE_PRIVATE)

    override fun getClicks(): Int {
        return sharedPreferences.getInt(AD_ID, 0)
    }

    override fun addClick() {
        var clicks = sharedPreferences.getInt(AD_ID, 0)
        clicks++
        sharedPreferences.edit().putInt(AD_ID, clicks).apply()
    }

    override fun setClick() {
        sharedPreferences.edit().putInt(AD_ID, 0).apply()
    }


}