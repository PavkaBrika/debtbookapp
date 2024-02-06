package sharedprefs

import android.content.Context
import com.breckneck.deptbook.data.storage.SettingsStorage
import com.breckneck.deptbook.domain.util.DEBT_QUANTITY_FOR_NEXT_SHOW
import util.ORDER_DEBT_BY_DATE
import util.ORDER_HUMAN_BY_DATE

private const val SHARED_PREFS_SETTINGS = "shared_prefs_settings"
private const val MAIN_CURRENCY_FIRST = "main_currency_first" // currency that shown statistics in main fragment
private const val MAIN_CURRENCY_SECOND = "main_currency_second" // currency that shown statistics in main fragment
private const val DEFAULT_CURRENCY = "default_currency" // start currency in switch in NewDebtFragment
private const val SUM_IN_SHARE_TEXT = "sum_in_share_text" // enable sum for each debt
private const val APP_RATE = "app_rate"
private const val APP_THEME = "app_theme"
private const val DEBTS_QUANTITY_FOR_APP_RATE_DIALOG_SHOW = "debts_quantity_for_app_rate_dialog_show"
private const val DEBT_ORDER_ATTRIBUTE = "debt_order_attribute"
private const val DEBT_ORDER_BY_INCREASE = "debt_order_by_increase"
private const val HUMAN_ORDER_ATTRIBUTE = "human_order_attribute"
private const val HUMAN_ORDER_BY_INCREASE = "human_order_by_increase"


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

    override fun setDebtQuantityForAppRateDialogShow(quantity: Int) {
        sharedPreferences.edit().putInt(DEBTS_QUANTITY_FOR_APP_RATE_DIALOG_SHOW, quantity).apply()
    }

    override fun getDebtQuantityForAppRateDialogShow(): Int {
        return sharedPreferences.getInt(DEBTS_QUANTITY_FOR_APP_RATE_DIALOG_SHOW, DEBT_QUANTITY_FOR_NEXT_SHOW)
    }

    override fun setDebtOrder(order: Pair<Int, Boolean>) {
        sharedPreferences.edit()
            .putInt(DEBT_ORDER_ATTRIBUTE, order.first)
            .putBoolean(DEBT_ORDER_BY_INCREASE, order.second)
            .apply()
    }

    override fun getDebtOrder(): Pair<Int, Boolean> {
        return Pair(
            sharedPreferences.getInt(DEBT_ORDER_ATTRIBUTE, ORDER_DEBT_BY_DATE),
            sharedPreferences.getBoolean(DEBT_ORDER_BY_INCREASE, true)
        )
    }

    override fun setHumanOrder(order: Pair<Int, Boolean>) {
        sharedPreferences.edit()
            .putInt(HUMAN_ORDER_ATTRIBUTE, order.first)
            .putBoolean(HUMAN_ORDER_BY_INCREASE, order.second)
            .apply()
    }

    override fun getHumanOrder(): Pair<Int, Boolean> {
        return Pair(
            sharedPreferences.getInt(HUMAN_ORDER_ATTRIBUTE, ORDER_HUMAN_BY_DATE),
            sharedPreferences.getBoolean(HUMAN_ORDER_BY_INCREASE, true)
        )
    }
}