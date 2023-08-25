package sharedprefs

import android.content.Context
import com.breckneck.deptbook.data.storage.SettingsStorage

private const val SHARED_PREFS_SETTINGS = "shared_prefs_settings"
private const val MAIN_CURRENCY_FIRST = "main_currency_first" // currency that shown statistics in main fragment
private const val MAIN_CURRENCY_SECOND = "main_currency_second" // currency that shown statistics in main fragment
private const val MAIN_EDIT_CURRENCY = "main_edit_currency" // start currency in switch in NewDebtFragment
private const val SUM_IN_SHARE_TEXT = "sum_in_share_text" // enable sum for each debt

class SharedPrefsSettingsStorageImpl(val context: Context): SettingsStorage {

    val sharedPreferences = context.getSharedPreferences(SHARED_PREFS_SETTINGS, Context.MODE_PRIVATE)

    override fun setFirstMainCurrency(currency: String) {
        sharedPreferences.edit().putString(MAIN_CURRENCY_FIRST, currency).apply()
    }

    override fun getFirstMainCurrency(): String {
        return sharedPreferences.getString(MAIN_CURRENCY_FIRST, "USD")!!
    }
}