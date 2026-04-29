package com.breckneck.deptbook.domain.usecase.Finance

import com.breckneck.deptbook.domain.model.Finance
import com.breckneck.deptbook.domain.repository.FinanceRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlinx.coroutines.test.runTest
import java.util.Date

class FinanceUseCasesTest {

    private val financeRepository = mock<FinanceRepository>()
    private val testDate = Date(0L)

    @AfterEach
    fun tearDown() {
        Mockito.reset(financeRepository)
    }

    private fun createFinance(id: Int = 0, sum: Double = 100.0, categoryId: Int = 1) =
        Finance(id = id, sum = sum, date = testDate, info = null, financeCategoryId = categoryId)

    // --- GetAllFinances ---

    @Test
    fun `GetAllFinances should return list from repository`() {
        val finances = listOf(createFinance(1, 100.0), createFinance(2, -50.0))
        Mockito.`when`(financeRepository.getAllFinance()).thenReturn(finances)
        val result = GetAllFinances(financeRepository).execute()
        Assertions.assertEquals(finances, result)
    }

    @Test
    fun `GetAllFinances should return empty list`() {
        Mockito.`when`(financeRepository.getAllFinance()).thenReturn(emptyList())
        val result = GetAllFinances(financeRepository).execute()
        Assertions.assertTrue(result.isEmpty())
    }

    // --- GetFinanceByCategoryId ---

    @Test
    fun `GetFinanceByCategoryId should return finances for given categoryId`() = runTest {
        val finances = listOf(createFinance(1, 200.0, 3))
        whenever(financeRepository.getFinanceByCategoryId(3)).thenReturn(finances)
        val result = GetFinanceByCategoryId(financeRepository).execute(categoryId = 3)
        Assertions.assertEquals(finances, result)
    }

    @Test
    fun `GetFinanceByCategoryId should return empty list for unknown categoryId`() = runTest {
        whenever(financeRepository.getFinanceByCategoryId(99)).thenReturn(emptyList())
        val result = GetFinanceByCategoryId(financeRepository).execute(categoryId = 99)
        Assertions.assertTrue(result.isEmpty())
    }

    // --- SetFinance ---

    @Test
    fun `SetFinance should call setFinance with provided finance`() = runTest {
        val finance = createFinance(0, 350.0, 2)
        SetFinance(financeRepository).execute(finance = finance)
        verify(financeRepository).setFinance(finance = finance)
    }

    @Test
    fun `SetFinance should call setFinance exactly once`() = runTest {
        SetFinance(financeRepository).execute(finance = createFinance())
        verify(financeRepository, Mockito.times(1)).setFinance(any())
    }

    // --- DeleteFinance ---

    @Test
    fun `DeleteFinance should call deleteFinance with provided finance`() = runTest {
        val finance = createFinance(5, 75.0)
        DeleteFinance(financeRepository).execute(finance = finance)
        verify(financeRepository).deleteFinance(finance = finance)
    }

    // --- UpdateFinance ---

    @Test
    fun `UpdateFinance should call updateFinance with provided finance`() = runTest {
        val finance = createFinance(3, 125.0, 4)
        UpdateFinance(financeRepository).execute(finance = finance)
        verify(financeRepository).updateFinance(finance = finance)
    }

    @Test
    fun `UpdateFinance should call updateFinance exactly once`() = runTest {
        UpdateFinance(financeRepository).execute(finance = createFinance())
        verify(financeRepository, Mockito.times(1)).updateFinance(any())
    }

    // --- DeleteAllFinancesByCategoryId ---

    @Test
    fun `DeleteAllFinancesByCategoryId should call deleteFinanceByCategoryId with correct id`() = runTest {
        DeleteAllFinancesByCategoryId(financeRepository).execute(financeCategoryId = 7)
        verify(financeRepository).deleteFinanceByCategoryId(financeCategoryId = 7)
    }

    @Test
    fun `DeleteAllFinancesByCategoryId should call deleteFinanceByCategoryId exactly once`() = runTest {
        DeleteAllFinancesByCategoryId(financeRepository).execute(financeCategoryId = 1)
        verify(financeRepository, Mockito.times(1)).deleteFinanceByCategoryId(1)
    }

    // --- ReplaceAllFinances ---

    @Test
    fun `ReplaceAllFinances should call replaceAllFinances with provided list`() {
        val finances = listOf(createFinance(1), createFinance(2))
        ReplaceAllFinances(financeRepository).execute(financeList = finances)
        verify(financeRepository).replaceAllFinances(financeList = finances)
    }

    @Test
    fun `ReplaceAllFinances should call replaceAllFinances with empty list`() {
        ReplaceAllFinances(financeRepository).execute(financeList = emptyList())
        verify(financeRepository).replaceAllFinances(financeList = emptyList())
    }
}
