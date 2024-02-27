package database

import android.content.Context
import androidx.room.Room
import com.breckneck.deptbook.data.storage.DebtStorage
import entity.Debt
import util.DATA_BASE_NAME

private const val SHARED_PREFS_DEBT = "shared_prefs_name_2"
private const val DEBT_ID = "debtid"

class DataBaseDebtStorageImpl(context: Context): DebtStorage {

    val sharedPreferences = context.getSharedPreferences(SHARED_PREFS_DEBT, Context.MODE_PRIVATE)
    val db = Room.databaseBuilder(context, AppDataBase::class.java, DATA_BASE_NAME).build()

    override fun getAllDebtsById(id: Int): List<Debt> {
        return db.appDao().getAllDebtsById(id = id)
    }

    override fun setDebt(debt: Debt) {
        var debtId = sharedPreferences.getInt(DEBT_ID, 0)
        debt.id = debtId
        db.appDao().insertDebt(debt)
        debtId++
        sharedPreferences.edit().putInt(DEBT_ID ,debtId).apply()
    }

    override fun deleteDebt(debt: Debt) {
        db.appDao().deleteDebt(debt)
    }

    override fun editDebt(debt: Debt) {
        db.appDao().updateDebt(debt)
    }

    override fun deleteDebtsByHumanId(id: Int) {
        db.appDao().deleteHumanById(id = id)
    }

    override fun getDebtQuantity(): Int {
        return db.appDao().getDebtQuantity()
    }
}