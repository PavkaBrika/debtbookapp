package database

import androidx.room.*
import entity.Debt
import entity.FinanceCategoryData
import entity.FinanceData
import entity.GoalData
import entity.GoalDepositData
import entity.Human
import entity.relations.FinanceCategoryDataWithFinanceData
import util.FinanceCategoryStateData


@Dao
interface AppDao {

    //Human
    @Query("SELECT * FROM human")
    fun getAllHuman(): List<Human>

    @Query("DELETE FROM human")
    fun deleteAllHumans()

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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllHumans(humanList: List<Human>)

    @Delete
    fun deleteHuman(human: Human)

    @Update
    fun updateHuman(human: Human)

    //Debt
    @Query("SELECT * FROM debt")
    fun getAllDebts(): List<Debt>

    @Query("DELETE FROM debt")
    fun deleteAllDebts()

    @Query("SELECT * FROM debt WHERE idHuman = :id")
    fun getAllDebtsById(id: Int): List<Debt>

    @Query("DELETE FROM debt WHERE idHuman = :id")
    fun deleteDebtsByHumanId(id: Int)

    @Query("SELECT count(*) FROM debt")
    fun getDebtQuantity(): Int

    @Insert
    fun insertDebt(debt: Debt)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllDebts(debtList: List<Debt>)

    @Delete
    fun deleteDebt(debt: Debt)

    @Update
    fun updateDebt(debt: Debt)

    //Finance
    @Query("SELECT * from financedata")
    fun getAllFinances(): List<FinanceData>

    @Query("SELECT * from financedata WHERE financeCategoryId = :categoryId")
    fun getFinanceByCategoryId(categoryId: Int): List<FinanceData>

    @Query("DELETE FROM financeData WHERE financeCategoryId = :financeCategoryId")
    fun deleteAllFinancesByCategoryId(financeCategoryId: Int)

    @Query("DELETE FROM financedata")
    fun deleteAllFinances()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllFinances(financeDataList: List<FinanceData>)

    @Insert
    fun insertFinance(financeData: FinanceData)

    @Delete
    fun deleteFinance(financeData: FinanceData)

    @Update
    fun updateFinance(financeData: FinanceData)

    //Finance category
    @Query("SELECT * FROM financecategorydata")
    fun getAllFinanceCategories(): List<FinanceCategoryData>

    @Query("SELECT * FROM financecategorydata WHERE state = :financeCategoryStateData")
    fun getFinanceCategoriesByState(financeCategoryStateData: FinanceCategoryStateData): List<FinanceCategoryData>

    @Transaction
    @Query("SELECT * FROM financecategorydata WHERE state = :financeCategoryStateData")
    fun getFinanceCategoriesWithFinancesByState(financeCategoryStateData: FinanceCategoryStateData): List<FinanceCategoryDataWithFinanceData>

    @Transaction
    @Query("SELECT * FROM financecategorydata")
    fun getAllFinanceCategoriesWithFinances(): List<FinanceCategoryDataWithFinanceData>

    @Query("DELETE FROM financecategorydata")
    fun deleteAllFinanceCategories()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllFinanceCategories(financeCategoryDataList: List<FinanceCategoryData>)

    @Insert
    fun insertFinanceCategory(financeCategoryData: FinanceCategoryData)

    @Delete
    fun deleteFinanceCategory(financeCategoryData: FinanceCategoryData)

    @Update
    fun updateFinanceCategory(financeCategoryData: FinanceCategoryData)

    //GOAL

    @Query("SELECT * FROM GoalData")
    fun getAllGoals(): List<GoalData>

    @Insert
    fun insertGoal(goalData: GoalData)

    @Delete
    fun deleteGoal(goalData: GoalData)

    @Update
    fun updateGoal(goalData: GoalData)

    //GOAL DEPOSIT

    @Query("SELECT * FROM GoalDepositData WHERE goalId = :goalId")
    fun getGoalDepositsByGoalId(goalId: Int): List<GoalDepositData>

    @Insert
    fun insertGoalDeposit(goalDepositData: GoalDepositData)

    @Delete
    fun deleteGoalDeposit(goalDepositData: GoalDepositData)

    @Update
    fun updateGoalDeposit(goalDepositData: GoalDepositData)
}