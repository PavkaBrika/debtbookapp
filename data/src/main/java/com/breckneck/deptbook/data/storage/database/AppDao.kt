package com.breckneck.deptbook.data.storage.database

import androidx.room.*
import com.breckneck.deptbook.data.storage.entity.Debt
import com.breckneck.deptbook.data.storage.entity.Human


@Dao
interface AppDao {

    //Human
    @Query("SELECT * FROM human")
    fun getAllHuman(): List<Human>

    @Insert
    fun insertHuman(human: Human)

    @Delete
    fun deleteHuman(human: Human)

    @Update
    fun updateHuman(human: Human)

    //Debt
    @Query("SELECT * FROM debt")
    fun getAllDebts(): List<Debt>

    @Query("SELECT * FROM debt WHERE idHuman = :id")
    fun getAllDebtsById(id: Int): List<Debt>

    @Insert
    fun insertDebt(debt: Debt)

    @Delete
    fun deleteDebt(debt: Debt)

    @Update
    fun updateDebt(debt: Debt)

}