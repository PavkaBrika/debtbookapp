package com.breckneck.deptbook.data.storage.database

import androidx.room.*
import com.breckneck.deptbook.data.storage.entity.Debt
import com.breckneck.deptbook.data.storage.entity.Human


@Dao
interface AppDao {

    //Human
    @Query("SELECT * FROM human")
    fun getAllHuman(): List<Human>

    @Query("SELECT * FROM human WHERE sumDebt >= 0")
    fun getPositiveHumans(): List<Human>

    @Query("SELECT * FROM human WHERE sumDebt <= 0")
    fun getNegativeHumans(): List<Human>

    @Query("SELECT id FROM human ORDER BY id DESC LIMIT 1")
    fun getLastHumanId() : Int

    @Query("UPDATE human SET sumDebt = sumDebt + :sum WHERE id = :humanId")
    fun addSum(humanId: Int, sum: Double)

    @Query("SELECT sumDebt from human WHERE currency = :currency")
    fun getAllDebtsSum(currency: String): List<Double>

    @Query("SELECT sumDebt from human WHERE id = :humanId")
    fun getHumanSumDebt(humanId: Int): Double

    @Query("DELETE FROM human WHERE id = :id")
    fun deleteHumanById(id: Int)

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

    @Query("DELETE FROM debt WHERE idHuman = :id")
    fun deleteDebtsByHumanId(id: Int)

    @Insert
    fun insertDebt(debt: Debt)

    @Delete
    fun deleteDebt(debt: Debt)

    @Update
    fun updateDebt(debt: Debt)

}