package database

import android.content.Context
import androidx.room.Room
import com.breckneck.deptbook.data.storage.DebtStorage
import entity.Debt

private const val DEBT_ID = "debtid"

class DataBaseDebtStorageImpl(context: Context): DebtStorage {

    val db = Room.databaseBuilder(context, AppDataBase::class.java, "HumanDataBase").build()

    override fun getAllDebtsById(id: Int): List<Debt> {
        return db.appDao().getAllDebtsById(id = id)
    }

    override fun setDebt(debt: Debt) {
        db.appDao().insertDebt(debt)
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