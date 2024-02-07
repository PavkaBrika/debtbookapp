package sharedprefs

import android.content.Context
import com.breckneck.deptbook.data.storage.AdStorage

private val SHARED_PREFS_ADS = "shared_prefs_name_3"
private val AD_ID = "adid"

class SharedPrefsAdStorageImpl(context: Context): AdStorage {

    val sharedPreferences = context.getSharedPreferences(SHARED_PREFS_ADS, Context.MODE_PRIVATE)

    override fun getClicks(): Int {
        return sharedPreferences.getInt(AD_ID, 0)
    }

    override fun saveClick(click: Int) {
        sharedPreferences.edit().putInt(AD_ID, click).apply()
    }
}