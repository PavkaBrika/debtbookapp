package com.breckneck.deptbook.data.storage.database

import android.content.Context
import androidx.room.Room
import com.breckneck.deptbook.data.storage.DebtStorage
import com.breckneck.deptbook.data.storage.entity.Debt

private val SHARED_PREFS_NAME_2 = "shared_prefs_name_2"
private val DEBT_ID = "debtid"

class DataBaseDebtStorageImpl(context: Context): DebtStorage {

    val sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME_2, Context.MODE_PRIVATE)
    val db = Room.databaseBuilder(context, AppDataBase::class.java, "HumanDataBase").build()

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


}