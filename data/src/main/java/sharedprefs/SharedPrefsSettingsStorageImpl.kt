package sharedprefs

import android.content.Context
import com.breckneck.deptbook.data.storage.SettingsStorage

private const val SHARED_PREFS_SETTINGS = "shared_prefs_settings"
private const val MAIN_CURRENCY_FIRST = "main_currency_first" // currency that shown statistics in main fragment
private const val MAIN_CURRENCY_SECOND = "main_currency_second" // currency that shown statistics in main fragment
private const val DEFAULT_CURRENCY = "default_currency" // start currency in switch in NewDebtFragment
private const val SUM_IN_SHARE_TEXT = "sum_in_share_text" // enable sum for each debt
private const val APP_RATE = "app_rate"
private const val APP_THEME = "app_theme"

class SharedPrefsSettingsStorageImpl(val context: Context): SettingsStorage {

    val sharedPreferences = context.getSharedPreferences(SHARED_PREFS_SETTINGS, Context.MODE_PRIVATE)

    override fun setFirstMainCurrency(currency: String) {
        sharedPreferences.edit().putString(MAIN_CURRENCY_FIRST, currency).apply()
    }

    override fun getFirstMainCurrency(): String {
        return sharedPreferences.getString(MAIN_CURRENCY_FIRST, "USD")!!
    }

    override fun setSecondMainCurrency(currency: String) {
        sharedPreferences.edit().putString(MAIN_CURRENCY_SECOND, currency).apply()
    }

    override fun getSecondMainCurrency(): String {
        return sharedPreferences.getString(MAIN_CURRENCY_SECOND, "EUR")!!
    }

    override fun setDefaultCurrency(currency: String) {
        sharedPreferences.edit().putString(DEFAULT_CURRENCY, currency).apply()
    }

    override fun getDefaultCurrency(): String {
        return sharedPreferences.getString(DEFAULT_CURRENCY, "USD")!!
    }

    override fun setAddSumInShareText(addSumInShareText: Boolean) {
        sharedPreferences.edit().putBoolean(SUM_IN_SHARE_TEXT, addSumInShareText).apply()
    }

    override fun getAddSumInShareText(): Boolean {
        return sharedPreferences.getBoolean(SUM_IN_SHARE_TEXT, false)
    }

    override fun setAppIsRated(isRated: Boolean) {
        sharedPreferences.edit().putBoolean(APP_RATE, isRated).apply()
    }

    override fun getAppIsRated(): Boolean {
        return sharedPreferences.getBoolean(APP_RATE, false)
    }

    override fun setAppTheme(theme: String) {
        sharedPreferences.edit().putString(APP_THEME, theme).apply()
    }

    override fun getAppTheme(): String {
        return sharedPreferences.getString(APP_THEME, "System")!!
    }
}