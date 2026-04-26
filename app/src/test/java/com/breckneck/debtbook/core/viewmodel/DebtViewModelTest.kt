package com.breckneck.debtbook.core.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.breckneck.debtbook.debt.main.DebtViewModel
import com.breckneck.deptbook.domain.usecase.Human.GetAllDebtsSumUseCase
import com.breckneck.deptbook.domain.usecase.Human.GetAllHumansUseCase
import com.breckneck.deptbook.domain.usecase.Human.UpdateHuman
import com.breckneck.deptbook.domain.usecase.Settings.GetFirstMainCurrency
import com.breckneck.deptbook.domain.usecase.Settings.GetHumanOrder
import com.breckneck.deptbook.domain.usecase.Settings.GetSecondMainCurrency
import com.breckneck.deptbook.domain.usecase.Settings.SetHumanOrder
import com.breckneck.deptbook.domain.util.HumanOrderAttribute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class DebtViewModelTest {

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    private val getAllHumansUseCase = Mockito.mock(GetAllHumansUseCase::class.java)
    private val getAllDebtsSumUseCase = Mockito.mock(GetAllDebtsSumUseCase::class.java)
    private val getFirstMainCurrency = Mockito.mock(GetFirstMainCurrency::class.java)
    private val getSecondMainCurrency = Mockito.mock(GetSecondMainCurrency::class.java)
    private val updateHuman = Mockito.mock(UpdateHuman::class.java)
    private val setHumanOrder = Mockito.mock(SetHumanOrder::class.java)
    private val getHumanOrder = Mockito.mock(GetHumanOrder::class.java)

    private lateinit var viewModel: DebtViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        whenever(getHumanOrder.execute()).thenReturn(Pair(HumanOrderAttribute.Sum, true))
        whenever(getFirstMainCurrency.execute()).thenReturn("$")
        whenever(getSecondMainCurrency.execute()).thenReturn("$")
        whenever(getAllDebtsSumUseCase.execute(anyString(), anyString())).thenReturn(Pair("0", "0"))
        whenever(getAllHumansUseCase.execute()).thenReturn(emptyList())
        viewModel = DebtViewModel(
            getAllHumansUseCase = getAllHumansUseCase,
            getAllDebtsSumUseCase = getAllDebtsSumUseCase,
            getFirstMainCurrency = getFirstMainCurrency,
            getSecondMainCurrency = getSecondMainCurrency,
            getHumanOrder = getHumanOrder,
            setHumanOrder = setHumanOrder,
            updateHuman = updateHuman
        )
    }

    @After
    fun tearDown() {
        Mockito.reset(
            getAllHumansUseCase,
            getAllDebtsSumUseCase,
            getFirstMainCurrency,
            getSecondMainCurrency,
            updateHuman,
            setHumanOrder,
            getHumanOrder,
        )
        Dispatchers.resetMain()
    }

    @Test
    fun `getHumanOrder should update humanOrder LiveData from use case`() {
        val expectedOrder = Pair(HumanOrderAttribute.Sum, true)
        Mockito.`when`(getHumanOrder.execute()).thenReturn(expectedOrder)

        viewModel.getHumanOrder()

        assertEquals(expectedOrder, viewModel.humanOrder.value)
    }

    @Test
    fun `getHumanOrder should update humanOrder with Date attribute`() {
        val expectedOrder = Pair(HumanOrderAttribute.Date, false)
        Mockito.`when`(getHumanOrder.execute()).thenReturn(expectedOrder)

        viewModel.getHumanOrder()

        assertEquals(expectedOrder, viewModel.humanOrder.value)
    }

    @Test
    fun `saveHumanOrder should delegate to setHumanOrder use case`() {
        val order = Pair(HumanOrderAttribute.Sum, false)
        viewModel.saveHumanOrder(order = order)
        Mockito.verify(setHumanOrder).execute(order = order)
    }

    @Test
    fun `setIsSearching should update isSearching LiveData`() {
        viewModel.setIsSearching(true)
        assertEquals(true, viewModel.isSearching.value)

        viewModel.setIsSearching(false)
        assertEquals(false, viewModel.isSearching.value)
    }

    @Test
    fun `onHumanSortDialogOpen should set isSortDialogOpened to true`() {
        viewModel.onHumanSortDialogOpen()
        assertEquals(true, viewModel.isSortDialogOpened.value)
    }

    @Test
    fun `onHumanSortDialogClose should set isSortDialogOpened to false`() {
        viewModel.onHumanSortDialogOpen()
        viewModel.onHumanSortDialogClose()
        assertEquals(false, viewModel.isSortDialogOpened.value)
    }
}
