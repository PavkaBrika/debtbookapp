package com.breckneck.deptbook.domain.usecase.GoalDeposit

import com.breckneck.deptbook.domain.model.GoalDeposit
import com.breckneck.deptbook.domain.repository.GoalDepositRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import java.util.Date

class GoalDepositUseCasesTest {

    private val goalDepositRepository = mock<GoalDepositRepository>()
    private val testDate = Date(0L)

    @AfterEach
    fun tearDown() {
        Mockito.reset(goalDepositRepository)
    }

    private fun createDeposit(id: Int = 0, goalId: Int = 1, sum: Double = 100.0) =
        GoalDeposit(id = id, sum = sum, date = testDate, goalId = goalId)

    // --- GetAllGoalDeposits ---

    @Test
    fun `GetAllGoalDeposits should return list from repository`() {
        val deposits = listOf(createDeposit(1, 1), createDeposit(2, 1))
        Mockito.`when`(goalDepositRepository.getAllGoalDeposits()).thenReturn(deposits)
        val result = GetAllGoalDeposits(goalDepositRepository).execute()
        Assertions.assertEquals(deposits, result)
    }

    @Test
    fun `GetAllGoalDeposits should return empty list`() {
        Mockito.`when`(goalDepositRepository.getAllGoalDeposits()).thenReturn(emptyList())
        val result = GetAllGoalDeposits(goalDepositRepository).execute()
        Assertions.assertTrue(result.isEmpty())
    }

    // --- GetGoalDepositsByGoalId ---

    @Test
    fun `GetGoalDepositsByGoalId should return deposits for given goalId`() {
        val deposits = listOf(createDeposit(1, 5), createDeposit(2, 5))
        Mockito.`when`(goalDepositRepository.getGoalDepositsByGoalId(5)).thenReturn(deposits)
        val result = GetGoalDepositsByGoalId(goalDepositRepository).execute(goalId = 5)
        Assertions.assertEquals(deposits, result)
    }

    @Test
    fun `GetGoalDepositsByGoalId should return empty list for unknown goalId`() {
        Mockito.`when`(goalDepositRepository.getGoalDepositsByGoalId(99)).thenReturn(emptyList())
        val result = GetGoalDepositsByGoalId(goalDepositRepository).execute(goalId = 99)
        Assertions.assertTrue(result.isEmpty())
    }

    // --- SetGoalDeposit ---

    @Test
    fun `SetGoalDeposit should call setGoalDeposit with provided deposit`() {
        val deposit = createDeposit(0, 3, 250.0)
        SetGoalDeposit(goalDepositRepository).execute(goalDeposit = deposit)
        verify(goalDepositRepository).setGoalDeposit(goalDeposit = deposit)
    }

    @Test
    fun `SetGoalDeposit should call setGoalDeposit exactly once`() {
        SetGoalDeposit(goalDepositRepository).execute(goalDeposit = createDeposit())
        verify(goalDepositRepository, Mockito.times(1)).setGoalDeposit(any())
    }

    // --- DeleteGoalDepositsByGoalId ---

    @Test
    fun `DeleteGoalDepositsByGoalId should call deleteGoalDepositsByGoalId with correct id`() {
        DeleteGoalDepositsByGoalId(goalDepositRepository).execute(goalId = 7)
        verify(goalDepositRepository).deleteGoalDepositsByGoalId(goalId = 7)
    }

    @Test
    fun `DeleteGoalDepositsByGoalId should call deleteGoalDepositsByGoalId exactly once`() {
        DeleteGoalDepositsByGoalId(goalDepositRepository).execute(goalId = 1)
        verify(goalDepositRepository, Mockito.times(1)).deleteGoalDepositsByGoalId(1)
    }

    // --- ReplaceAllGoalsDeposits ---

    @Test
    fun `ReplaceAllGoalsDeposits should call replaceAllGoalDeposits with provided list`() {
        val deposits = listOf(createDeposit(1), createDeposit(2))
        ReplaceAllGoalsDeposits(goalDepositRepository).execute(goalDepositList = deposits)
        verify(goalDepositRepository).replaceAllGoalDeposits(goalDepositList = deposits)
    }

    @Test
    fun `ReplaceAllGoalsDeposits should call replaceAllGoalDeposits with empty list`() {
        ReplaceAllGoalsDeposits(goalDepositRepository).execute(goalDepositList = emptyList())
        verify(goalDepositRepository).replaceAllGoalDeposits(goalDepositList = emptyList())
    }
}
