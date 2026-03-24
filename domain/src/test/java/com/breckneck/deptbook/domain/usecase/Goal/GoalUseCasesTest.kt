package com.breckneck.deptbook.domain.usecase.Goal

import com.breckneck.deptbook.domain.model.Goal
import com.breckneck.deptbook.domain.repository.GoalRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import java.util.Date

class GoalUseCasesTest {

    private val goalRepository = mock<GoalRepository>()
    private val testDate = Date(0L)

    @AfterEach
    fun tearDown() {
        Mockito.reset(goalRepository)
    }

    private fun createGoal(id: Int = 0, name: String = "Goal $id") =
        Goal(id = id, name = name, sum = 1000.0, savedSum = 0.0, currency = "USD",
            photoPath = null, creationDate = testDate, goalDate = null)

    // --- GetAllGoals ---

    @Test
    fun `GetAllGoals should return list from repository`() {
        val goals = listOf(createGoal(1, "Car"), createGoal(2, "House"))
        Mockito.`when`(goalRepository.getAllGoals()).thenReturn(goals)
        val result = GetAllGoals(goalRepository).execute()
        Assertions.assertEquals(goals, result)
    }

    @Test
    fun `GetAllGoals should return empty list`() {
        Mockito.`when`(goalRepository.getAllGoals()).thenReturn(emptyList())
        val result = GetAllGoals(goalRepository).execute()
        Assertions.assertTrue(result.isEmpty())
    }

    // --- SetGoal ---

    @Test
    fun `SetGoal should call setGoal with provided goal`() {
        val goal = createGoal(0, "New Car")
        SetGoal(goalRepository).execute(goal = goal)
        verify(goalRepository).setGoal(goal = goal)
    }

    @Test
    fun `SetGoal should call setGoal exactly once`() {
        SetGoal(goalRepository).execute(goal = createGoal())
        verify(goalRepository, Mockito.times(1)).setGoal(any())
    }

    // --- UpdateGoal ---

    @Test
    fun `UpdateGoal should call updateGoal with provided goal`() {
        val goal = createGoal(5, "Vacation")
        UpdateGoal(goalRepository).execute(goal = goal)
        verify(goalRepository).updateGoal(goal = goal)
    }

    @Test
    fun `UpdateGoal should call updateGoal exactly once`() {
        UpdateGoal(goalRepository).execute(goal = createGoal())
        verify(goalRepository, Mockito.times(1)).updateGoal(any())
    }

    // --- DeleteGoal ---

    @Test
    fun `DeleteGoal should call deleteGoal with provided goal`() {
        val goal = createGoal(3, "Laptop")
        DeleteGoal(goalRepository).execute(goal = goal)
        verify(goalRepository).deleteGoal(goal = goal)
    }

    @Test
    fun `DeleteGoal should call deleteGoal exactly once`() {
        DeleteGoal(goalRepository).execute(goal = createGoal())
        verify(goalRepository, Mockito.times(1)).deleteGoal(any())
    }

    // --- ReplaceAllGoals ---

    @Test
    fun `ReplaceAllGoals should call replaceAllGoals with provided list`() {
        val goals = listOf(createGoal(1), createGoal(2))
        ReplaceAllGoals(goalRepository).execute(goalList = goals)
        verify(goalRepository).replaceAllGoals(goalList = goals)
    }

    @Test
    fun `ReplaceAllGoals should call replaceAllGoals with empty list`() {
        ReplaceAllGoals(goalRepository).execute(goalList = emptyList())
        verify(goalRepository).replaceAllGoals(goalList = emptyList())
    }
}
