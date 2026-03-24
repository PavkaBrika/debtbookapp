package com.breckneck.deptbook.domain.usecase.FinanceCategory

import com.breckneck.deptbook.domain.model.FinanceCategory
import com.breckneck.deptbook.domain.model.FinanceCategoryWithFinances
import com.breckneck.deptbook.domain.repository.FinanceCategoryRepository
import com.breckneck.deptbook.domain.util.FinanceCategoryState
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.any

class FinanceCategoryUseCasesTest {

    private val financeCategoryRepository = mock<FinanceCategoryRepository>()

    @AfterEach
    fun tearDown() {
        Mockito.reset(financeCategoryRepository)
    }

    private fun createCategory(id: Int = 0, state: FinanceCategoryState = FinanceCategoryState.EXPENSE) =
        FinanceCategory(id = id, name = "Category $id", state = state, color = "#FF0000", image = 0)

    // --- GetAllFinanceCategories ---

    @Test
    fun `GetAllFinanceCategories should return list from repository`() {
        val categories = listOf(createCategory(1), createCategory(2, FinanceCategoryState.INCOME))
        Mockito.`when`(financeCategoryRepository.getAllFinanceCategories()).thenReturn(categories)
        val result = GetAllFinanceCategories(financeCategoryRepository).execute()
        Assertions.assertEquals(categories, result)
    }

    @Test
    fun `GetAllFinanceCategories should return empty list`() {
        Mockito.`when`(financeCategoryRepository.getAllFinanceCategories()).thenReturn(emptyList())
        val result = GetAllFinanceCategories(financeCategoryRepository).execute()
        Assertions.assertTrue(result.isEmpty())
    }

    // --- GetAllCategoriesWithFinances ---

    @Test
    fun `GetAllCategoriesWithFinances should return mutable list from repository`() {
        val category = createCategory(1)
        val withFinances = listOf(FinanceCategoryWithFinances(financeCategory = category, financeList = mutableListOf()))
        Mockito.`when`(financeCategoryRepository.getAllCategoriesWithFinances()).thenReturn(withFinances)
        val result = GetAllCategoriesWithFinances(financeCategoryRepository).execute()
        Assertions.assertEquals(withFinances, result)
        Assertions.assertInstanceOf(MutableList::class.java, result)
    }

    // --- GetFinanceCategoriesByState ---

    @Test
    fun `GetFinanceCategoriesByState should return expense categories`() {
        val expenseCategories = listOf(createCategory(1, FinanceCategoryState.EXPENSE))
        Mockito.`when`(
            financeCategoryRepository.getFinanceCategoriesByState(FinanceCategoryState.EXPENSE)
        ).thenReturn(expenseCategories)
        val result = GetFinanceCategoriesByState(financeCategoryRepository)
            .execute(financeCategoryState = FinanceCategoryState.EXPENSE)
        Assertions.assertEquals(expenseCategories, result)
    }

    @Test
    fun `GetFinanceCategoriesByState should return income categories`() {
        val incomeCategories = listOf(createCategory(2, FinanceCategoryState.INCOME))
        Mockito.`when`(
            financeCategoryRepository.getFinanceCategoriesByState(FinanceCategoryState.INCOME)
        ).thenReturn(incomeCategories)
        val result = GetFinanceCategoriesByState(financeCategoryRepository)
            .execute(financeCategoryState = FinanceCategoryState.INCOME)
        Assertions.assertEquals(incomeCategories, result)
    }

    // --- SetFinanceCategory ---

    @Test
    fun `SetFinanceCategory should call setFinanceCategory with provided category`() {
        val category = createCategory(0)
        SetFinanceCategory(financeCategoryRepository).execute(financeCategory = category)
        verify(financeCategoryRepository).setFinanceCategory(category = category)
    }

    @Test
    fun `SetFinanceCategory should call setFinanceCategory exactly once`() {
        SetFinanceCategory(financeCategoryRepository).execute(financeCategory = createCategory())
        verify(financeCategoryRepository, Mockito.times(1)).setFinanceCategory(any())
    }

    // --- DeleteFinanceCategory ---

    @Test
    fun `DeleteFinanceCategory should call deleteFinanceCategory with provided category`() {
        val category = createCategory(3)
        DeleteFinanceCategory(financeCategoryRepository).execute(financeCategory = category)
        verify(financeCategoryRepository).deleteFinanceCategory(category = category)
    }

    // --- ReplaceAllFinanceCategories ---

    @Test
    fun `ReplaceAllFinanceCategories should call replaceAllFinanceCategories with provided list`() {
        val categories = listOf(createCategory(1), createCategory(2))
        ReplaceAllFinanceCategories(financeCategoryRepository).execute(financeCategoryList = categories)
        verify(financeCategoryRepository).replaceAllFinanceCategories(financeCategoriesList = categories)
    }

    @Test
    fun `ReplaceAllFinanceCategories should call replaceAllFinanceCategories with empty list`() {
        ReplaceAllFinanceCategories(financeCategoryRepository).execute(financeCategoryList = emptyList())
        verify(financeCategoryRepository).replaceAllFinanceCategories(financeCategoriesList = emptyList())
    }
}
